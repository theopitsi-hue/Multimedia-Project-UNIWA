package org.theopitsi.multimedia.server.connection;

import org.theopitsi.multimedia.common.packet.HeartbeatPacket;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;
import org.theopitsi.multimedia.server.connection.packet.PacketHandler;
import org.theopitsi.multimedia.server.connection.packet.PacketManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private boolean exiting = false;
    private final int PORT = 5000;

    private static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();

    public ConnectionManager() {

    }

    public static List<ClientHandler> getAllClients() {
        return clients.values().stream().toList();
    }

    //captures incoming client communication
    public void beginListening() {
        Thread heartbeatThread = new Thread(() -> {
            while (!exiting) {
                try {
                    PacketManager.sendToAllClients(new HeartbeatPacket.Request());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
        //ServerSocket serverSocket = null;
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        int i = 1;

        try {
            serverSocket = new ServerSocket(PORT);
            MMServer.logger.info("Server listening on port " + PORT);
        } catch (IOException e) {
            MMServer.logger.warning("Error starting server: " + e.getMessage());
        }

        while (!exiting) {
            try {
                assert serverSocket != null;
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new ClientHandler(clientSocket,i).start();
            i++;
        }
    }

    public void exit(){
        exiting = true;
    }

    public static void register(int id, ClientHandler handler) {
        clients.put(id, handler);
        MMServer.logger.info("Client with id " + id + " connected, total: "+clients.size());
        PacketHandler.OnClientConnected(id);
    }

    public static void remove(int id) {
        clients.remove(id);
        MMServer.logger.info("Client with id " + id + " disconnected, total: "+clients.size());
    }

    public static ClientHandler getClient(int id) {
        return clients.get(id);
    }

}
