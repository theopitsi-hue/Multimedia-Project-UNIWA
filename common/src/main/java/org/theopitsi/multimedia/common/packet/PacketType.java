package org.theopitsi.multimedia.common.packet;

public class PacketType {                                       //  desc                                / side
    public static final int DISCONNECT = 0;                     // Disconnect from server               / client -> server

    public static final int HEARTBEAT_REQ = 1;                  // Heartbeat to monitor client          / server -> client
    public static final int HEARTBEAT_RESP = 5;                 // Heartbeat response with stats          / client -> server

    public static final int VIDEO_LIST_RESP = 2;                // Sends the available video list       / server -> client
    public static final int VIDEO_LIST_REQ = 3;                 // Requests the available video list    / client -> server

    public static final int BANDWIDTH_RESP = 4;                 // Requests user's bandwidth            / server -> client
    public static final int BANDWIDTH_REQ = 6;                  // replies user's bandwidth            / client -> server
}
