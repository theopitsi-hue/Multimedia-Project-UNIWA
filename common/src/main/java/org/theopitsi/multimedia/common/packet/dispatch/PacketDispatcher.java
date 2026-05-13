package org.theopitsi.multimedia.common.packet.dispatch;

import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.PacketType;
import org.theopitsi.multimedia.common.packet.VideoListPacket;

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
                new DataInputStream(
                        new ByteArrayInputStream(payload));

        Packet packet = createPacket(type);

        //fill the packet with the incoming data
        packet.deserialize(packetIn);
        return packet;
    }

    //todo: replace later with a more automatic system, im not gonna add every packet by hand here!
    public static Packet createPacket(int type) {

        return switch (type) {

            case PacketType.VIDEO_LIST_REQ ->
                    new VideoListPacket.Request();

            case PacketType.VIDEO_LIST_RESP ->
                    new VideoListPacket.Response();

            default ->
                    throw new RuntimeException("Unknown packet type: " + type);
        };
    }
}
