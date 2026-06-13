package org.theopitsi.multimedia.common.packet;

import org.theopitsi.multimedia.common.data.TransmissionProtocolType;
import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoSelectPacket {
    public static class Response extends Packet{
        private int result;
        private TransmissionProtocolType protocol;

        public Response() {
        }

        public Response(int i, TransmissionProtocolType transmissionProtocolType) {
            protocol = transmissionProtocolType;
            result = i;
        }

        @Override
        public int getType() {
            return PacketType.VIDEO_RESP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(result);
            out.writeInt(protocol.ordinal());
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            result = in.readInt();
            protocol = TransmissionProtocolType.values()[in.readInt()];
        }

        public int getResult(){
            return result;
        }

        public String getProtocol() {
            return protocol.toString();
        }
    }

    public static class Request extends Packet{
        private VideoData selected;
        private String protocol;

        public Request(VideoData video,String protocol) {
            selected = video;
            this.protocol = protocol;
        }

        public Request() {
        }

        @Override
        public int getType() {
            return PacketType.VIDEO_REQ;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {

            out.writeUTF(selected.getFilename());
            out.writeInt(selected.getFormat().ordinal());
            out.writeInt(selected.getQuality().ordinal());

            out.writeUTF(protocol);

        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {

            String filename = in.readUTF();

            VideoFormatType format = VideoFormatType.values()[in.readInt()];
            VideoQualityType quality = VideoQualityType.values()[in.readInt()];

            selected = new VideoData(
                    filename,
                    format,
                    quality
            );

            protocol = in.readUTF();

        }

        @Override
        public int getResponceType() {
            return PacketType.VIDEO_LIST_RESP;
        }

        public VideoData getSelected() {
            return selected;
        }

        public String getProtocol(){
            return protocol;
        }
    }
}
