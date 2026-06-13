package org.theopitsi.multimedia.server.connection;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;
import org.theopitsi.multimedia.server.media.ContentManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContentStreamOutput {
    private volatile boolean exiting = false;
    private static final int STREAM_PORT = 5001;

    private ServerSocket streamCaptureSocket;

    //saves client streams
    private static final Map<Integer, Thread> streamThreads = new ConcurrentHashMap<>();

    //listens for incoming stream sockets from clients
    private void startStreamListener() {
        new Thread(() -> {
            try {
                streamCaptureSocket = new ServerSocket(STREAM_PORT);
                MMServer.logger.info("Stream server listening on " + STREAM_PORT);

                while (!exiting) {
                    Socket socket = streamCaptureSocket.accept();

                    new Thread(() -> handleCapturedStreamSocket(socket),
                            "stream-socket-handler").start();
                }

            } catch (IOException e) {
                if (!exiting) {
                    MMServer.logger.warning("Stream socket error: " + e.getMessage());
                }
            }

        }, "stream-listener").start();
    }

    //handles any incoming stream sockets per client
    private void handleCapturedStreamSocket(Socket socket) {
        try{
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //IMPORTANT:
            //first message MUST be clientId from client!!!!
            int clientId = in.readInt();

            ClientHandler client = ConnectionManager.getClient(clientId);
            if (client == null) {
                MMServer.logger.warning("Unknown clientId on stream socket: " + clientId);
                return;
            }
            MMServer.logger.info("Client connected to streaming socket: "+clientId);
            client.setStreamOutput(out);

        } catch (Exception e) {
            MMServer.logger.warning("Stream socket error: " + e.getMessage());
        }

        try {
            while (!exiting){
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //starts streaming to a specific client
    public void startStreamingTo(int clientId, VideoData selected) {
        ClientHandler client = ConnectionManager.getAllClients().get(clientId);
        if (client == null) {
            MMServer.logger.warning("Unknown clientId on stream socket: " + clientId);
            return;
        }

        client.setBeingStreamedTo(true);
        client.setWatching(selected);

        Thread streamThread = new Thread(() -> {
            try {
                streamLoop(clientId);
            } catch (IOException e) {
                MMServer.logger.warning("Stream error: " + e.getMessage());
            }
        }, "stream-client-" + clientId);

        streamThreads.put(clientId, streamThread);
        streamThread.start();
    }

    //actually sending the video data
    public void streamLoop(int clientId) throws IOException {
        ClientHandler client = ConnectionManager.getAllClients().get(clientId);
        MMServer.logger.warning("streamLoop " + clientId);
        if (client == null || client.watching == null) {
            MMServer.logger.warning("Stream not ready for client " + clientId);
            return;
        }

        DataOutputStream out = client.getStreamOut();

        File file = new File(ContentManager.videoDir + client.watching.toFileName());

        try (FileInputStream fis = new FileInputStream(file)) {
            out.writeLong(file.length());

            byte[] buffer = new byte[8192];
            int read;
            MMServer.logger.warning("OUTGOING file length: " + file.length() + " bytes");
            while (client.isBeingStreamedTo() && (read = fis.read(buffer)) != -1) {
                out.writeInt(read);
                out.write(buffer, 0, read);
                out.flush();
            }

            out.writeInt(-1);
            out.flush();
        }
    }

    //stops sending video data, if it was sending anything.
    public void stopStreamingTo(int clientId) {
        ClientHandler client = ConnectionManager.getAllClients().get(clientId);
        if (client != null) {
            client.setBeingStreamedTo(false);
            client.setWatching(null);
        }

        Thread t = streamThreads.remove(clientId);
        if (t != null) t.interrupt();
    }

    public void exit() {
        exiting = true;
        try {
            if (streamCaptureSocket != null) streamCaptureSocket.close();
        } catch (IOException ignored) {
            MMServer.logger.warning("Stream socket error: " + ignored.getMessage());
        }
    }

    public void beginListening() {
        startStreamListener();
    }

    public Thread remove(int id) {
        return streamThreads.remove(id);
    }
}
