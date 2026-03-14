package org.theopitsi.multimedia.client;

import org.theopitsi.multimedia.MMClient;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private final String identifier;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public Client(String name){
        this.identifier = name;
    }

    public void connect(String addr, int port){
        //create a connection
        try {
            socket = new Socket(addr, port);
            MMClient.logger.info("Connected");

            //takes input from terminal
            in = socket.getInputStream();

            //sends output to the socket
            out = socket.getOutputStream();

            out.write(identifier.getBytes());
            out.flush();

            in.close();
            out.close();
            socket.close();
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }
    }

    public void disconnect(){
        try {
            in.close();
            out.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
