package org.theopitsi.multimedia.client.data;

import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.client.packet.PacketHandler;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.VideoListPacket;
import org.theopitsi.multimedia.common.packet.dispatch.PacketDispatcher;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String identifier;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(String name){
        this.identifier = name;
    }

    public void connect(String addr, int port) throws IOException {
        try {
            socket = new Socket(addr, port);
            MMClient.logger.info("Connected");

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            PacketDispatcher.write(out,new VideoListPacket.Request());
                out.flush();

//
//            int i = 20;
//
//            while (i > 0) {
//
//                Thread.sleep(3000);
//
//                PacketDispatcher.write(out,new VideoListPacket.Request());
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
//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        listen();
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

    //listen for incoming packets
    private void listen() throws IOException {
        // main loop
        while (true) {
            try {
                Packet incomingPacket = PacketDispatcher.read(in);
                PacketHandler.OnPacketReceived(incomingPacket);
            } catch (IOException e) {
                return;
            }
        }
    }
}
