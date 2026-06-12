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

    // STREAM SOCKET
    private Socket streamSocket;
    private DataOutputStream streamOut;
    private DataInputStream streamIn;

    private int clientId = 1;

    public Client(String name) {
        this.identifier = name;
    }

    public void connect(String addr, int controlPort, int streamPort) throws IOException {

        // CONTROL CONNECTION
        controlSocket = new Socket(addr, controlPort);

        out = new DataOutputStream(controlSocket.getOutputStream());
        in = new DataInputStream(controlSocket.getInputStream());

        MMClient.logger.info("Control connected");

        PacketHandler.OnConnected(this);

        listenControl();

        // STREAM CONNECTION (separate socket)
        streamSocket = new Socket(addr, streamPort);

        streamOut = new DataOutputStream(streamSocket.getOutputStream());
        streamIn = new DataInputStream(streamSocket.getInputStream());

        // IMPORTANT: register session with server
        streamOut.writeInt(clientId);
        streamOut.flush();

        MMClient.logger.info("Stream connected");
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (controlSocket != null) controlSocket.close();

            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (streamSocket != null) streamSocket.close();

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

    public synchronized boolean send(Packet packet) {
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