package org.theopitsi.multimedia.common.packet.dispatch;

import org.theopitsi.multimedia.common.packet.*;

import java.io.*;

public class PacketDispatcher {

    public static void write(DataOutputStream out, Packet packet) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream tempOut = new DataOutputStream(baos);

        packet.serialize(tempOut);
        byte[] payload = baos.toByteArray();

        //write packet wrapper with format [type][length][payload]
            out.writeInt(packet.getType());
            out.writeInt(payload.length);
            out.write(payload);
            out.flush();
    }

    public static Packet read(DataInputStream in) throws IOException {
        //[type][length][payload]

        //unpack type header
        int type = in.readInt();
        int length = in.readInt();

        //read payload
        byte[] payload = new byte[length];
        in.readFully(payload);

        DataInputStream packetIn =
                new DataInputStream(new ByteArrayInputStream(payload));

        Packet packet = createPacket(type);

        //fill the packet with the incoming data
        packet.deserialize(packetIn);
        return packet;
    }

    //todo: replace later with a more automatic system, im not gonna add every packet by hand here!
    //new me: guess what bro
    public static Packet createPacket(int type) {

        return switch (type) {

            case PacketType.VIDEO_LIST_REQ ->
                    new VideoListPacket.Request();

            case PacketType.VIDEO_LIST_RESP ->
                    new VideoListPacket.Response();

            case PacketType.HEARTBEAT_REQ ->
                    new HeartbeatPacket.Request();

            case PacketType.HEARTBEAT_RESP ->
                    new HeartbeatPacket.Response();

            case PacketType.BANDWIDTH_RESP ->
                    new BandwidthPacket.Response();

            case PacketType.BANDWIDTH_REQ ->
                    new BandwidthPacket.Request();

            case PacketType.VIDEO_REQ ->
                    new VideoSelectPacket.Request();

            case PacketType.VIDEO_RESP ->
                    new VideoSelectPacket.Response();

            case PacketType.STREAM_STOP ->
                    new StreamStopPacket.Request();

            default ->
                    throw new RuntimeException("Unknown packet type: " + type);
        };
    }
}
