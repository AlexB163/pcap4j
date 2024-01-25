package org.pcap4j.core;

import java.nio.ByteBuffer;

public interface NioRawPacketListener {

    void gotPacket(ByteBuffer packet);
}
