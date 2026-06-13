package org.theopitsi.multimedia.common.packet;

import org.theopitsi.multimedia.common.data.VideoFormatType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BandwidthPacket {

    public static class Request extends Packet {

        @Override
        public int getType() {
            return PacketType.BANDWIDTH_REQ;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            // no payload
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            // no payload
        }

        @Override
        public int getResponceType() {
            return PacketType.BANDWIDTH_RESP;
        }
    }

    public static class Response extends Packet {

        private double bandwidthMbps;
        private int format;

        public Response() {}

        public Response(double bandwidthMbps, int format) {
            this.bandwidthMbps = bandwidthMbps;
            this.format = format;
        }

        public double getBandwidthMbps() {
            return bandwidthMbps;
        }

        public VideoFormatType getFormat(){
            return VideoFormatType.values()[format];
        }

        @Override
        public int getType() {
            return PacketType.BANDWIDTH_RESP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            out.writeDouble(bandwidthMbps);
            out.writeInt(format);
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            bandwidthMbps = in.readDouble();
            format = in.readInt();
        }

        @Override
        public int getResponceType() {
            return -1;
        }
    }
}