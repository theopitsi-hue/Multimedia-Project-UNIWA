package org.theopitsi.multimedia.server.connection;

import org.theopitsi.multimedia.MMServer;
import org.theopitsi.multimedia.server.connection.user.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager {
    private boolean exiting = false;
    private final int PORT = 5000;
    private final int maxUsers;

    public ConnectionManager(int maxUsers){
        this.maxUsers = maxUsers;
    }

    public void beginListening() {
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

}
