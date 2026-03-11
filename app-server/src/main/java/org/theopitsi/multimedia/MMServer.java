package org.theopitsi.multimedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class MMServer {
    public static Logger logger = Logger.getLogger("MM-SERVER");
    private static boolean exiting = false;
    private static final int PORT = 5000;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            logger.info("Server listening on port " + PORT);

            while (true) {
                if (exiting){
                    clean();
                    break;
                }
                captureClients(serverSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /// Stalls execution to wait for client connections.
    /// Once the client connects, creates a thread to handle them seperately
    /// so, it can keep waiting for more clients.
    private static void captureClients(ServerSocket serverSocket){
        try {
            //create a socket to accept the client connection
            Socket clientSocket = serverSocket.accept();
            logger.info("Client connected: " + clientSocket.getInetAddress());

            //handle client in a separate thread
            new Thread(() -> handleClient(clientSocket)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clean(){

    }

    private static void exit(){
        exiting = true;
    }
}
