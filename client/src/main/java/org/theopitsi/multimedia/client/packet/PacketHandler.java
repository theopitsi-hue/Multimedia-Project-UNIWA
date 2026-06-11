package org.theopitsi.multimedia.client.packet;

import fr.bmartel.speedtest.SpeedTestSocket;
import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.client.data.Client;
import org.theopitsi.multimedia.common.packet.*;

import static org.theopitsi.multimedia.client.MMClient.speedTestSocket;

public class PacketHandler {
    //packets received from server
    public static void OnPacketReceived(Client client, Packet packet){
        MMClient.logger.info("client received packet: #"+packet.getType());

        if (packet.getType() == PacketType.VIDEO_LIST_RESP) {
            VideoListPacket.Response res = (VideoListPacket.Response) packet;
            MMClient.logger.info("Received video list data: ");
            MMClient.logger.info(res.getData().get(0).toString());
        }

        if (packet.getType()==PacketType.HEARTBEAT_REQ){
            client.send(new HeartbeatPacket.Response());
            //MMClient.logger.info("Heartbeat on client!");
        }

        if (packet.getType() == PacketType.BANDWIDTH_REQ){

            speedTestSocket.startDownload("http://speedtest.ams1.nl.leaseweb.net/10mb.bin");
        }
    }

    public static void OnConnected(Client client) {
        client.send(new VideoListPacket.Request());
    }
}
