package org.theopitsi.multimedia.common.packet;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HandshakePacket {
    public static class Response extends Packet{
        private int result;

        public Response(int res) {
            result = res;
        }

        public Response() {
        }

        @Override
        public int getType() {
            return PacketType.HANDSHAKE_RESP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(result);
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            result = in.readInt();
        }

        public int getResult(){
            return result;
        }
    }

    public static class Request extends Packet{
        public Request() {
        }

        @Override
        public int getType() {
            return PacketType.HANDSHAKE_REQ;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {

        }

        @Override
        public int getResponceType() {
            return PacketType.HANDSHAKE_REQ;
        }

    }
}
