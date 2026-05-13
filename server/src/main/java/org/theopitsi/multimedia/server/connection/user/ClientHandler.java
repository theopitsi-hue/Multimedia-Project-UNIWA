package org.theopitsi.multimedia.server.connection.user;

import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.dispatch.PacketDispatcher;
import org.theopitsi.multimedia.server.MMServer;

import java.io.*;
import java.net.*;

//Operates for each client connected to the server
public class ClientHandler extends Thread {
    private Socket client;
    private final int index;

    public ClientHandler(Socket socket, int i) {
        this.client = socket;
        this.index = i;
    }

    @Override
    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;

        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());

            var ip = client.getInetAddress().getHostAddress();
            var port = client.getPort();

            MMServer.logger.info("Client connected: " + ip + ":" + port);

        } catch (IOException e) {
            MMServer.logger.warning(e.getLocalizedMessage());
            e.printStackTrace();
            return;
        }

        // main loop
        while (true) {
            try {
                Packet incomingPacket = PacketDispatcher.read(in);

                MMServer.logger.info("Packet received: #" + incomingPacket.getType());
                MMServer.logger.info("Packet Type:" + incomingPacket.getClass());

//                String message = in.readUTF();
//
//                MMServer.logger.info("Client: " + message);

            } catch (IOException e) {
                // includes disconnects, reset, etc.
                MMServer.logger.warning("Connection lost: " + e.getMessage());
                return;
            }
        }
    }
}