package org.theopitsi.multimedia.common.packet;

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

        public Response() {}

        public Response(double bandwidthMbps) {
            this.bandwidthMbps = bandwidthMbps;
        }

        public double getBandwidthMbps() {
            return bandwidthMbps;
        }

        @Override
        public int getType() {
            return PacketType.BANDWIDTH_RESP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            out.writeDouble(bandwidthMbps);
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            bandwidthMbps = in.readDouble();
        }

        @Override
        public int getResponceType() {
            return -1;
        }
    }
}