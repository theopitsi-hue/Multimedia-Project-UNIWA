package org.theopitsi.multimedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MMClient {

    public static void main(String[] args) {
        String host = "localhost"; // server IP
        int port = 5000;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
        ) {

            String userInput = "BOOOOP";
            out.println(userInput); // send to server
            System.out.println(in.readLine()); // read response

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
