package org.theopitsi.multimedia.client.packet;

import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.PacketType;
import org.theopitsi.multimedia.common.packet.VideoListPacket;

import java.util.List;

public class PacketHandler {

    //packets received from server
    public static void OnPacketReceived(Packet packet){
        MMClient.logger.info("client received packed: #"+packet.getType());

        if (packet.getType() == PacketType.VIDEO_LIST_RESP) {
            VideoListPacket.Response res = (VideoListPacket.Response) packet;
            MMClient.logger.info("Received video list data: ");
            MMClient.logger.info(res.getData().get(0).toString());
        }
    }
}
