package org.theopitsi.multimedia.server.connection.packet;

import org.theopitsi.multimedia.common.data.TransmissionProtocolType;
import org.theopitsi.multimedia.common.data.VideoQualityType;
import org.theopitsi.multimedia.common.packet.*;
import org.theopitsi.multimedia.server.MMServer;
import org.theopitsi.multimedia.server.connection.ConnectionManager;

public class PacketHandler {

    //packets received from a specific client
    public static void OnPacketReceived(int clientId, Packet packet){
        //MMServer.logger.info(clientId+" - Packet received: #" + packet.getType());
        //MMServer.logger.info(clientId+" - Packet Type:" + packet.getClass());

        if (packet.getType() == PacketType.BANDWIDTH_RESP) {
            BandwidthPacket.Response resp = ( BandwidthPacket.Response) packet;

            MMServer.logger.info("Client "+clientId+" bandwidth: "+resp.getBandwidthMbps()+" mbps");
            ConnectionManager.getClient(clientId).setLastDownSpeed(resp.getBandwidthMbps());
            PacketManager.sendToClient(clientId, new VideoListPacket.Response(MMServer.contentManager.getFilteredVideos(resp.getBandwidthMbps())));

            return;
        }

        if (packet.getType() == PacketType.VIDEO_REQ) {
            //CHECK VIDEO AVAILABILITY

            VideoSelectPacket.Request a = (VideoSelectPacket.Request) packet;
            MMServer.logger.info("Client "+clientId+" requested: "+a.getSelected());

            if (!a.getProtocol().equals("ProjectAuto")) {
                // do custom tcp
                MMServer.logger.info("STARTING CUSTOM STREAMING");
                MMServer.connectionStreamOutput.startStreamingTo(clientId, a.getSelected());
                PacketManager.sendToClient(clientId, new VideoSelectPacket.Response(0, TransmissionProtocolType.TCP));
            }else{
                //do FFMPEG server
                MMServer.logger.info("STARTING FFMPEG STREAMING");
                MMServer.streamManager.streamForClient(clientId,a.getSelected(), a.getSelected().getQuality().getRecomendedProtocol().toString().toLowerCase());
                PacketManager.sendToClient(clientId, new VideoSelectPacket.Response(1,a.getSelected().getQuality().getRecomendedProtocol()));
            }
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
