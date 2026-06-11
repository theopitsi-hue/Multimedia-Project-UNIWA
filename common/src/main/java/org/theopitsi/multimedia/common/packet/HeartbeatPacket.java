package org.theopitsi.multimedia.common.packet;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeartbeatPacket {
    public static class Response extends Packet{
        public Response(int bd) {

        }

        public Response() {
        }

        @Override
        public int getType() {
            return PacketType.HEARTBEAT_RESP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {

        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            //int size = in.readInt();
        }
    }

    public static class Request extends Packet{
        @Override
        public int getType() {
            return PacketType.HEARTBEAT_REQ;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {

        }

        @Override
        public int getResponceType() {
            return PacketType.HEARTBEAT_RESP;
        }
    }
}
