package org.theopitsi.multimedia.server.connection.packet;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;
import org.theopitsi.multimedia.common.packet.*;
import org.theopitsi.multimedia.server.MMServer;

import java.util.List;

public class PacketHandler {

    //packets received from a specific client
    public static void OnPacketReceived(int clientId, Packet packet){
        //MMServer.logger.info(clientId+" - Packet received: #" + packet.getType());
        //MMServer.logger.info(clientId+" - Packet Type:" + packet.getClass());

        if (packet.getType() == PacketType.VIDEO_LIST_REQ) {
            PacketManager.sendToClient(clientId, new VideoListPacket.Response(MMServer.contentManager.getVideos()));
        }

        if (packet.getType() == PacketType.BANDWIDTH_RESP) {
            BandwidthPacket.Response resp = ( BandwidthPacket.Response) packet;

            MMServer.logger.info("Client "+clientId+" bandwidth: "+resp.getBandwidthMbps()+" mbps");
            return;
        }

        if (packet.getType() == PacketType.VIDEO_REQ) {
            //CHECK VIDEO AVAILABILITY

            VideoSelectPacket.Request a = (VideoSelectPacket.Request) packet;
            MMServer.logger.info("Client "+clientId+" requested: "+a.getSelected());
            MMServer.connectionStreamOutput.startStreamingTo(clientId, a.getSelected());
            PacketManager.sendToClient(clientId, new VideoSelectPacket.Response(0));
        }

        if (packet.getType() == PacketType.STREAM_STOP){
            MMServer.logger.info("Client "+clientId+" stopped stream.");
            MMServer.connectionStreamOutput.stopStreamingTo(clientId);
        }

        if (packet.getType() == PacketType.HANDSHAKE_REQ){
            PacketManager.sendToClient(clientId, new HandshakePacket.Response(clientId));
        }
    }

    public static void OnClientConnected(int id) {
        PacketManager.sendToClient(id, new BandwidthPacket.Request());
    }
}
