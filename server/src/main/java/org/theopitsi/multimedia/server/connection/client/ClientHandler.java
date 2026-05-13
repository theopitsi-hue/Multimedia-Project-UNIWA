package org.theopitsi.multimedia.server.connection.client;

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

    public ClientHandler(Socket socket, int i) {
        this.clientSocket = socket;
        this.index = i;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            var ip = clientSocket.getInetAddress().getHostAddress();
            var port = clientSocket.getPort();

            MMServer.logger.info("Client connected: " + ip + ":" + port);
            ConnectionManager.register(index,this);
        } catch (IOException e) {
            MMServer.logger.warning(e.getLocalizedMessage());
            e.printStackTrace();
            ConnectionManager.remove(index);
            return;
        }


        try {
            listen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionManager.remove(index);

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
//                if (incomingPacket.getResponceType() == -1) return;
//
//                //create a response packet and fill it with data
//                Packet response = PacketDispatcher.createPacket(incomingPacket.getResponceType());
//
//                //send it back to the client
//                PacketDispatcher.write(out,response);
//                out.flush();

            } catch (IOException e) {
                // includes disconnects, reset, etc.
                MMServer.logger.warning("Connection lost: " + e.getMessage());
                ConnectionManager.remove(index);
                return;
            }
        }
    }
}