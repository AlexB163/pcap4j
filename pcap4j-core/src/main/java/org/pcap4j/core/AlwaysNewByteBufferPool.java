package org.pcap4j.core;

import java.nio.ByteBuffer;

public class AlwaysNewByteBufferPool implements NioRawPacketPool {

    private static final AlwaysNewByteBufferPool INSTANCE = new AlwaysNewByteBufferPool();

    public static NioRawPacketPool getInstance() {
        return INSTANCE;
    }

    @Override
    public ByteBuffer borrow(int caplen) {
        return ByteBuffer.allocateDirect(caplen);
    }

    @Override
    public void release(ByteBuffer buffer) {

    }
}
