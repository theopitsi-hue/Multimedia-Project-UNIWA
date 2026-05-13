package org.theopitsi.multimedia.common.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public class PacketManager {
    public static void sendPacket(DataOutputStream out, int type, byte[] payload) throws IOException {
        out.writeInt(type);

        out.writeInt(payload.length);

        out.write(payload);

        out.flush();
    }
}
