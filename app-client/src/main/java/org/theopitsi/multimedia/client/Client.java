package org.theopitsi.multimedia.client;

import org.theopitsi.multimedia.MMClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(){
    }

    public void connect(String addr, int port){
        //create a connection
        try {
            socket = new Socket(addr, port);
            MMClient.logger.info("Connected");

            //takes input from terminal
            in = new DataInputStream(System.in);

            //sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
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
