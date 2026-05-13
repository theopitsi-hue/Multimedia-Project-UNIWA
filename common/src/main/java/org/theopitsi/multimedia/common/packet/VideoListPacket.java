package org.theopitsi.multimedia.common.packet;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoListPacket{
    public static class Response extends Packet{
        private List<VideoData> data = new ArrayList<>();

        public Response(List<VideoData> videos) {
            data = videos;
        }

        public Response() {
        }

        @Override
        public int getType() {
            return PacketType.VIDEO_LIST_RESP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(data.size());

            for (VideoData video : data) {

                out.writeUTF(video.getFilename());
                out.writeInt(video.getFormat().ordinal());
                out.writeInt(video.getQuality().ordinal());
            }
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
            int size = in.readInt();

            data = new ArrayList<>();

            for (int i = 0; i < size; i++) {

                String title = in.readUTF();

                VideoFormatType format = VideoFormatType.values()[in.readInt()];
                VideoQualityType quality = VideoQualityType.values()[in.readInt()];

                data.add(new VideoData(title, format,quality));
            }
        }

        public List<VideoData> getData() {
            return data;
        }
    }

    public static class Request extends Packet{
        @Override
        public int getType() {
            return PacketType.VIDEO_LIST_REQ;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
            //maybe have like filters/searching
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {

        }

        @Override
        public int getResponceType() {
            return PacketType.VIDEO_LIST_RESP;
        }
    }
}
