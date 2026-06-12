package org.theopitsi.multimedia.common.packet;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StreamStopPacket {
    public static class Request extends Packet{

        public Request() {
        }

        @Override
        public int getType() {
            return PacketType.STREAM_STOP;
        }

        @Override
        public void serialize(DataOutputStream out) throws IOException {
        }

        @Override
        public void deserialize(DataInputStream in) throws IOException {
        }

        @Override
        public int getResponceType() {
            return PacketType.STREAM_STOP;
        }
    }
}
