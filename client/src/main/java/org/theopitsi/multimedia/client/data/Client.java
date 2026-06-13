package org.theopitsi.multimedia.client.data;

import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.client.packet.PacketHandler;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.dispatch.PacketDispatcher;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String identifier;

    // CONTROL SOCKET
    private Socket controlSocket;
    private DataInputStream in;
    private DataOutputStream out;

    private volatile int clientId = 0;

    private final ContentStreamReceiver contentStreamIntake;

    public Client(String name) {
        this.identifier = name;
        contentStreamIntake = new ContentStreamReceiver();
    }

    public void connect(String addr, int controlPort) throws IOException {
        // CONTROL CONNECTION
        controlSocket = new Socket(addr, controlPort);

        out = new DataOutputStream(controlSocket.getOutputStream());
        in = new DataInputStream(controlSocket.getInputStream());

        MMClient.logger.info("Control socket created");

        PacketHandler.OnConnectedToServer(this);
        listenControl();
    }


    public void setClientId(int id){
        clientId = id;
        new Thread(() -> {
            try {
                contentStreamIntake.connect(id, "localhost", 5001);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (controlSocket != null) controlSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenControl() {
        new Thread(() -> {

            while (true) {
                try {
                    Packet incoming = PacketDispatcher.read(in);
                    PacketHandler.OnPacketReceived(this, incoming);

                } catch (IOException e) {
                    MMClient.logger.warning("Control connection lost");
                    return;
                }
            }

        }, "control-listener").start();
    }

    public synchronized boolean sendToServer(Packet packet) {
        try {
            PacketDispatcher.write(out, packet);
            out.flush();
            return true;

        } catch (IOException e) {
            MMClient.logger.warning("Failed to send packet");
            return false;
        }
    }
}