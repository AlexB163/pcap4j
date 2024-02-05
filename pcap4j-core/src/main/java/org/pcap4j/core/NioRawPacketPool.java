package org.pcap4j.core;

import java.nio.ByteBuffer;

public interface NioRawPacketPool {

    ByteBuffer borrow(int caplen);

    void release(ByteBuffer buffer);

}
