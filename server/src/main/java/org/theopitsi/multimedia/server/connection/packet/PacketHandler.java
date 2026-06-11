package org.theopitsi.multimedia.server.connection.packet;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;
import org.theopitsi.multimedia.common.packet.BandwidthPacket;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.common.packet.PacketType;
import org.theopitsi.multimedia.common.packet.VideoListPacket;
import org.theopitsi.multimedia.server.MMServer;

import java.util.List;

public class PacketHandler {

    //packets received from a specific client
    public static void OnPacketReceived(int clientId, Packet packet){
        MMServer.logger.info(clientId+"- Packet received: #" + packet.getType());
        MMServer.logger.info(clientId+"- Packet Type:" + packet.getClass());

        if (packet.getType() == PacketType.VIDEO_LIST_REQ) {
            PacketManager.sendToClient(clientId, new VideoListPacket.Response(List.of(new VideoData("test!!!!", VideoFormatType.AVI, VideoQualityType.p360))));
        }

        if (packet.getType() == PacketType.BANDWIDTH_RESP) {
            BandwidthPacket.Response resp = ( BandwidthPacket.Response) packet;

            MMServer.logger.info("Client "+clientId+" bandwidth: "+resp.getBandwidthMbps()+" mbps");
        }
    }

    public static void OnClientConnected(int id) {
        PacketManager.sendToClient(id, new BandwidthPacket.Request());
    }
}
