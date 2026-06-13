package org.theopitsi.multimedia.server.connection;

import org.theopitsi.multimedia.common.packet.HeartbeatPacket;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;
import org.theopitsi.multimedia.server.connection.packet.PacketHandler;
import org.theopitsi.multimedia.server.connection.packet.PacketManager;

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

    private ServerSocket controlSocket;
    private static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();

    private int idCounter = 0;

    public static List<ClientHandler> getAllClients() {
        return clients.values().stream().toList();
    }

    public void beginListening() {
        startHeartbeat();

        startControlListener();
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

    // Sends heartbeat signals to clients
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
        } catch (IOException ignored) {
            MMServer.logger.warning("Stream socket error: " + ignored.getMessage());
        }
    }

    public static void registerClient(int id, ClientHandler handler) {
        clients.put(id, handler);

        MMServer.logger.info("Client " + id + " connected. Total: " + clients.size());

        PacketHandler.OnClientConnected(id);
        serverController.setClients(clients.values().stream().toList());
    }

    public static void removeClient(int id) {
        clients.remove(id);

        Thread t = MMServer.connectionStreamOutput.remove(id);
        if (t != null) t.interrupt();

        serverController.setClients(clients.values().stream().toList());
    }

    public static ClientHandler getClient(int id) {
        return clients.get(id);
    }
}