/*_##########################################################################
  _##
  _##  Copyright (C) 2012 Pcap4J.org
  _##
  _##########################################################################
*/
package org.pcap4j.test.packet;

import org.junit.jupiter.api.Test;
import org.pcap4j.packet.ArpPacket.Builder;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.EthernetPacket.EthernetHeader;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ArpHardwareType;
import org.pcap4j.packet.namednumber.ArpOperation;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("javadoc")
public class EthernetPacketTest extends AbstractPacketTest {

    private static final Logger logger = LoggerFactory.getLogger(EthernetPacketTest.class);

    private final EthernetPacket packet;
    private final MacAddress dstAddr;
    private final MacAddress srcAddr;
    private final EtherType type;
    private final byte[] pad;

    public EthernetPacketTest() {
        this.dstAddr = MacAddress.ETHER_BROADCAST_ADDRESS;
        this.srcAddr = MacAddress.getByName("fe:00:00:00:00:01");
        this.type = EtherType.ARP;
        this.pad =
                new byte[]{
                        (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1,
                        (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1,
                        (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1,
                        (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1,
                        (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 0, (byte) 1,
                };

        Builder ab = new Builder();
        try {
            ab.hardwareType(ArpHardwareType.ETHERNET)
                    .protocolType(EtherType.IPV4)
                    .hardwareAddrLength((byte) MacAddress.SIZE_IN_BYTES)
                    .protocolAddrLength((byte) ByteArrays.INET4_ADDRESS_SIZE_IN_BYTES)
                    .srcHardwareAddr(srcAddr)
                    .dstHardwareAddr(dstAddr)
                    .srcProtocolAddr(
                            InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 0, (byte) 2, (byte) 1}))
                    .dstProtocolAddr(
                            InetAddress.getByAddress(new byte[]{(byte) 192, (byte) 0, (byte) 2, (byte) 2}))
                    .operation(ArpOperation.REQUEST);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }

        EthernetPacket.Builder eb = new EthernetPacket.Builder();
        eb.dstAddr(dstAddr)
                .srcAddr(srcAddr)
                .type(type)
                .payloadBuilder(ab)
                .pad(pad)
                .paddingAtBuild(false);
        this.packet = eb.build();
    }

    @Override
    protected Packet getPacket() {
        return packet;
    }

    @Override
    protected Packet getWholePacket() {
        return packet;
    }

    @Test
    public void testNewPacket() {
        try {
            EthernetPacket p =
                    EthernetPacket.newPacket(packet.getRawData(), 0, packet.getRawData().length);
            assertEquals(packet, p);
        } catch (IllegalRawDataException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void testNewPacketRandom() {
        RandomPacketTester.testClass(EthernetPacket.class, packet);
    }

    @Test
    public void testGetHeader() {
        EthernetHeader h = packet.getHeader();
        assertEquals(dstAddr, h.getDstAddr());
        assertEquals(srcAddr, h.getSrcAddr());
        assertEquals(type, h.getType());
        assertArrayEquals(pad, packet.getPad());
    }
}
