package org.theopitsi.multimedia.server.connection;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.packet.HeartbeatPacket;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;
import org.theopitsi.multimedia.server.connection.packet.PacketHandler;
import org.theopitsi.multimedia.server.connection.packet.PacketManager;
import org.theopitsi.multimedia.server.media.ContentManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.theopitsi.multimedia.server.MMServer.serverController;

public class ConnectionManager {

    private volatile boolean exiting = false;

    private static final int CONTROL_PORT = 5000;
    private static final int STREAM_PORT = 5001;

    private ServerSocket controlSocket;
    private ServerSocket streamSocket;

    private static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final Map<Integer, Thread> streamThreads = new ConcurrentHashMap<>();

    private int idCounter = 1;

    public static List<ClientHandler> getAllClients() {
        return clients.values().stream().toList();
    }

    public void beginListening() {
        startHeartbeat();

        startControlListener();
        startStreamListener();
    }

    private void startControlListener() {
        new Thread(() -> {
            try {
                controlSocket = new ServerSocket(CONTROL_PORT);
                MMServer.logger.info("Control server listening on " + CONTROL_PORT);

                while (!exiting) {
                    Socket clientSocket = controlSocket.accept();

                    ClientHandler handler = new ClientHandler(clientSocket, idCounter);
                    idCounter++;
                    handler.start();
                }

            } catch (IOException e) {
                if (!exiting) {
                    MMServer.logger.warning("Control socket error: " + e.getMessage());
                }
            }
        }, "control-listener").start();
    }

    private void startStreamListener() {
        new Thread(() -> {
            try {
                streamSocket = new ServerSocket(STREAM_PORT);
                MMServer.logger.info("Stream server listening on " + STREAM_PORT);

                while (!exiting) {

                    Socket socket = streamSocket.accept();

                    new Thread(() -> handleStreamSocket(socket),
                            "stream-socket-handler").start();
                }

            } catch (IOException e) {
                if (!exiting) {
                    MMServer.logger.warning("Stream socket error: " + e.getMessage());
                }
            }

        }, "stream-listener").start();
    }

    private void handleStreamSocket(Socket socket) {

        try (Socket s = socket;
             DataOutputStream out = new DataOutputStream(s.getOutputStream());
             DataInputStream in = new DataInputStream(s.getInputStream())) {

            //IMPORTANT:
            //first message MUST be clientId from client!!!!
            int clientId = in.readInt();

            ClientHandler client = clients.get(clientId);
            if (client == null) return;

            client.attachStreamOut(out);

            // keep socket alive while streaming
            while (!exiting && client.isBeingStreamedTo()) {
                Thread.sleep(50);
            }

        } catch (Exception e) {
            MMServer.logger.warning("Stream socket error: " + e.getMessage());
        }
    }

    private void startHeartbeat() {

        Thread heartbeat = new Thread(() -> {
            while (!exiting) {
                try {
                    PacketManager.sendToAllClients(new HeartbeatPacket.Request());
                    Thread.sleep(1000);
                } catch (Exception ignored) {}
            }
        }, "heartbeat");

        heartbeat.setDaemon(true);
        heartbeat.start();
    }

    public void exit() {
        exiting = true;

        try {
            if (controlSocket != null) controlSocket.close();
            if (streamSocket != null) streamSocket.close();
        } catch (IOException ignored) {}
    }

    public static void register(int id, ClientHandler handler) {
        clients.put(id, handler);

        MMServer.logger.info("Client " + id + " connected. Total: " + clients.size());

        PacketHandler.OnClientConnected(id);
        serverController.setClients(clients.values().stream().toList());
    }

    public static void remove(int id) {
        clients.remove(id);

        Thread t = streamThreads.remove(id);
        if (t != null) t.interrupt();

        serverController.setClients(clients.values().stream().toList());
    }

    public static ClientHandler getClient(int id) {
        return clients.get(id);
    }

    public void startStreamingTo(int clientId, VideoData selected) {

        ClientHandler client = clients.get(clientId);
        if (client == null) return;

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

    public void streamLoop(int clientId) throws IOException {
        ClientHandler client = clients.get(clientId);

        if (client == null || client.watching == null) {
            MMServer.logger.warning("Stream not ready for client " + clientId);
            return;
        }

        DataOutputStream out = client.getForStreamOut();

        File file = new File(ContentManager.videoDir + client.watching.toFileName());

        try (FileInputStream fis = new FileInputStream(file)) {

            out.writeUTF(file.getName());
            out.writeLong(file.length());

            byte[] buffer = new byte[8192];
            int read;

            while (client.isBeingStreamedTo()
                    && (read = fis.read(buffer)) != -1) {

                out.writeInt(read);
                out.write(buffer, 0, read);
            }

            out.writeInt(-1);
            out.flush();
        }
    }

    public void stopStreamingTo(int clientId) {

        ClientHandler client = clients.get(clientId);
        if (client != null) {
            client.setBeingStreamedTo(false);
            client.setWatching(null);
        }

        Thread t = streamThreads.remove(clientId);
        if (t != null) t.interrupt();
    }
}