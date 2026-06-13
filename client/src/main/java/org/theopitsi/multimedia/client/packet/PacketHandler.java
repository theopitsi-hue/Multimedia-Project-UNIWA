package org.theopitsi.multimedia.client.packet;

import javafx.application.Platform;
import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.client.data.Client;
import org.theopitsi.multimedia.common.packet.*;

import static org.theopitsi.multimedia.client.MMClient.speedTestSocket;

public class PacketHandler {
    //packets received from server
    public static void OnPacketReceived(Client client, Packet packet){
        //MMClient.logger.info("client received packet: #"+packet.getType());

        if (packet.getType() == PacketType.VIDEO_LIST_RESP) {
            VideoListPacket.Response res = (VideoListPacket.Response) packet;
            MMClient.logger.info("Received video list data.");

            Platform.runLater(() -> {
                MMClient.clientController.setVideoList(res.getData());
            });

        }

        if (packet.getType()==PacketType.HEARTBEAT_REQ){
            client.sendToServer(new HeartbeatPacket.Response());
            //MMClient.logger.info("Heartbeat on client!");
        }

        if (packet.getType() == PacketType.BANDWIDTH_REQ){
            //start a sample 10mb download to check speed.
            //sends a response packet to the server once its successful
            speedTestSocket.startDownload("http://speedtest.ams1.nl.leaseweb.net/10mb.bin");
        }

        if (packet.getType() == PacketType.VIDEO_RESP){
            VideoSelectPacket.Response res = (VideoSelectPacket.Response) packet;
            if (res.getResult() == 0) { //OK
                //start streaming
                MMClient.logger.info("Should start streaming.");
            }else if (res.getResult() == 1){
                MMClient.logger.info("capturing STREAM VIA FFMPEG.");
                MMClient.streamReceiver.beginCapturingStream(res.getProtocol());

            }else{
                throw new RuntimeException("SOMETHING WRONG!");
            }
            return;
        }

        if (packet.getType() == PacketType.HANDSHAKE_RESP){
            HandshakePacket.Response res = (HandshakePacket.Response) packet;
            MMClient.logger.info("Set clientid: "+res.getResult());
            client.setClientId(res.getResult());
        }
    }

    public static void OnConnectedToServer(Client client) {
        client.sendToServer(new HandshakePacket.Request());
        client.sendToServer(new VideoListPacket.Request());
    }
}
