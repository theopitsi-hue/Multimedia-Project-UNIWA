package org.theopitsi.multimedia.server.connection.client;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.dispatch.PacketDispatcher;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.ConnectionManager;
import org.theopitsi.multimedia.server.connection.packet.PacketHandler;

import java.io.*;
import java.net.*;

//Operates for each client connected to the server
public class ClientHandler extends Thread {
    private Socket clientSocket;

    public final int index;
    DataInputStream in = null;
    DataOutputStream out = null;

    public volatile boolean isBeingStreamedTo = false;
    public VideoData watching = null;

    private volatile boolean running = true;

    public ClientHandler(Socket socket, int i) {
        this.clientSocket = socket;
        this.index = i;

    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(
                    new BufferedOutputStream(clientSocket.getOutputStream())
            );

            var ip = clientSocket.getInetAddress().getHostAddress();
            var port = clientSocket.getPort();

            MMServer.logger.info("Client connected: " + ip + ":" + port);
            ConnectionManager.registerClient(index,this);

        } catch (IOException e) {
            MMServer.logger.warning(e.getLocalizedMessage());
            e.printStackTrace();
            ConnectionManager.removeClient(index);
            return;
        }


        try {
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionManager.removeClient(index);

            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    //directly send a packet to this client
    public boolean send(Packet packet) {
        try {
            PacketDispatcher.write(out, packet);
            out.flush();
            return true;
        } catch (IOException e) {
            MMServer.logger.warning("Send failed to client: " + index);
            return false;
        }
    }

    //listen for incoming packets
    private void listen() throws IOException {
        // main loop
        while (true) {
            try {
                Packet incomingPacket = PacketDispatcher.read(in);
                PacketHandler.OnPacketReceived(index,incomingPacket);
            } catch (IOException e) {
                // includes disconnects, reset, etc.
                MMServer.logger.warning("Connection lost: " + e.getMessage());
                ConnectionManager.removeClient(index);
                return;
            }
        }
    }

    public String getClientName(){
        return "Client "+index;
    }

    public String getCurrentlyWatching() {
        return watching == null?"idle":watching.getFilename();
    }

    public void setBeingStreamedTo(boolean se){
        isBeingStreamedTo = se;
    }

    public boolean isBeingStreamedTo() {
        return isBeingStreamedTo;
    }

    public void setWatching(VideoData selected) {
        watching = selected;
    }

    public DataOutputStream getOut() {
        return out;
    }

    private volatile DataOutputStream streamOut;

    public DataOutputStream getStreamOut() {
        return streamOut;
    }

    public void setStreamOutput(DataOutputStream outStream) {
        streamOut = outStream;
    }
}