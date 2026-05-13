package org.theopitsi.multimedia.server.connection.packet;

import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.ConnectionManager;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;

public class PacketManager {
    public static void sendToClient(int id, Packet packet){
        if (ConnectionManager.getClient(id) == null) return;

        ClientHandler handler = ConnectionManager.getClient(id);
        if (handler.send(packet)) {
            MMServer.logger.info("Attempted to send packet to user with id: " + id);
        }
    }

    public static void sendToAllClients(Packet packet){
        for(ClientHandler client:ConnectionManager.getAllClients()){
            client.send(packet);
        }
    }
}
