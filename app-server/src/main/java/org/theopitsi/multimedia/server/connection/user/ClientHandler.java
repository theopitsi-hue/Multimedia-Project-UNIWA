package org.theopitsi.multimedia.server.connection.user;

import org.theopitsi.multimedia.MMServer;

import java.io.*;
import java.net.*;

//Operates for each client connected to the server
public class ClientHandler extends Thread {
    private Socket client;

    public ClientHandler(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;

        try {
            //create an InputStream and an OutputStream to communicate with the client
            //todo: buffer these things
            brinp = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());


            var ip = client.getInetAddress().getHostAddress();
            var port = client.getPort();

            MMServer.logger.info("Client connected: " + ip + ":" + port);

        } catch (IOException e) {
            MMServer.logger.warning(e.getLocalizedMessage());
            e.printStackTrace();
        }

        String line;
        while (true) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    client.close();
                    return;
                } else {
                    MMServer.logger.info("Client: "+line+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}