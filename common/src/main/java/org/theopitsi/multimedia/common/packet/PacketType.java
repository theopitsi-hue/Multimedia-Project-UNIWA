package org.theopitsi.multimedia.common.packet;

public class PacketType {                                    //  desc                    /   side
    public static final int DISCONNECT = 0;                     // Disconnect from server   /   client -> server
    public static final int HEARTBEAT = 1;                      // Heartbeat to monitor health / client -> server

    public static final int VIDEO_LIST_RESP = 2;                 // Sends the available video list / server -> client
    public static final int VIDEO_LIST_REQ = 3;                 // Requests the available video list / client -> server

    public static final int REQ_VIDEO = 4;                      // Requests a specific video / client -> server



}
