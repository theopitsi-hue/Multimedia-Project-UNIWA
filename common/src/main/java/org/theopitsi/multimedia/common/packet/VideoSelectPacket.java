package org.theopitsi.multimedia.common.packet;

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

        public Response(int res) {
            result = res;
        }

        public Response() {
        }

        @Override
        public int getType() {
            return PacketType.VIDEO_RESP;
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
        private VideoData selected;

        public Request(VideoData video) {
            selected = video;
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
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {

            String filename = in.readUTF();

            VideoFormatType format =
                    VideoFormatType.values()[in.readInt()];

            VideoQualityType quality =
                    VideoQualityType.values()[in.readInt()];

            selected = new VideoData(
                    filename,
                    format,
                    quality
            );
        }

        @Override
        public int getResponceType() {
            return PacketType.VIDEO_LIST_RESP;
        }

        public VideoData getSelected() {
            return selected;
        }
    }
}
