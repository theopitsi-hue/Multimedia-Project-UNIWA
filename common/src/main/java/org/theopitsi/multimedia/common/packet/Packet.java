package org.theopitsi.multimedia.common.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {
    public abstract int getType();
    public abstract void serialize(DataOutputStream out) throws IOException;
    public abstract void deserialize(DataInputStream in) throws IOException;
}
