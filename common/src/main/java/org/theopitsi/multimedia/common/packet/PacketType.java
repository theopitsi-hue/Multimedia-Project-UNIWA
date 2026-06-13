package org.theopitsi.multimedia.common.packet;

public class PacketType {                                       //  desc                                / side
    public static final int DISCONNECT = 0;                     // Disconnect from server               / client -> server

    public static final int HEARTBEAT_REQ = 1;                  // Heartbeat to monitor client          / server -> client
    public static final int HEARTBEAT_RESP = 5;                 // Heartbeat response with stats        / client -> server

    public static final int VIDEO_LIST_RESP = 2;                // Sends the available video list       / server -> client
    public static final int VIDEO_LIST_REQ = 3;                 // Requests the available video list    / client -> server

    public static final int BANDWIDTH_RESP = 4;                 // Requests user's bandwidth            / server -> client
    public static final int BANDWIDTH_REQ = 6;                  // replies user's bandwidth             / client -> server

    public static final int VIDEO_REQ = 7;                      // Requests a specific video            / client -> server
    public static final int VIDEO_RESP = 8;                     // Accepts or denies it                 / server -> client
    public static final int VIDEO_CHUNK = 9;                    // chunk of the current streaming video / server -> client

    public static final int STREAM_STOP = 10;                    // client stops watching cur videop    / client -> server

    public static final int HANDSHAKE_RESP = 11;                 // responds with clientid              / server -> client
    public static final int HANDSHAKE_REQ = 12;                  // requests clientID                   / client -> server

}
