package org.theopitsi.multimedia.client.data;

import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.common.packet.VideoListPacket;
import org.theopitsi.multimedia.common.packet.dispatch.PacketDispatcher;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String identifier;

    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public Client(String name){
        this.identifier = name;
    }

    public void connect(String addr, int port){
        try {
            socket = new Socket(addr, port);
            MMClient.logger.info("Connected");

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());

            DataInputStream in =
                    new DataInputStream(socket.getInputStream());

//            // identify
//            out.writeUTF(identifier);
//            out.flush();

            //send request packet
            PacketDispatcher.write(out,new VideoListPacket.Request());
            out.flush();
//
//            int i = 20;
//
//            while (i > 0) {
//
//                Thread.sleep(3000);
//
//                out.writeUTF("tick!");
//                out.flush();
//
//                MMClient.logger.info("beat");
//
//                i--;
//            }

        } catch (IOException e) {
            System.out.println(e);
            return;
        }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
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
