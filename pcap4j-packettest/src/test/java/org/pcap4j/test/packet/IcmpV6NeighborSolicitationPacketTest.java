package org.pcap4j.test.packet;

import org.junit.jupiter.api.Test;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.packet.IcmpV6CommonPacket;
import org.pcap4j.packet.IcmpV6CommonPacket.IpV6NeighborDiscoveryOption;
import org.pcap4j.packet.IcmpV6NeighborSolicitationPacket;
import org.pcap4j.packet.IcmpV6NeighborSolicitationPacket.IcmpV6NeighborSolicitationHeader;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV6NeighborDiscoverySourceLinkLayerAddressOption.Builder;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.IpV6SimpleFlowLabel;
import org.pcap4j.packet.IpV6SimpleTrafficClass;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.SimpleBuilder;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IcmpV6Code;
import org.pcap4j.packet.namednumber.IcmpV6Type;
import org.pcap4j.packet.namednumber.IpNumber;
import org.pcap4j.packet.namednumber.IpVersion;
import org.pcap4j.util.MacAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("javadoc")
public class IcmpV6NeighborSolicitationPacketTest extends AbstractPacketTest {

    private static final Logger logger =
            LoggerFactory.getLogger(IcmpV6NeighborSolicitationPacketTest.class);

    private final IcmpV6NeighborSolicitationPacket packet;
    private final int reserved;
    private final Inet6Address targetAddress;
    private final List<IpV6NeighborDiscoveryOption> options =
            new ArrayList<IpV6NeighborDiscoveryOption>();

    public IcmpV6NeighborSolicitationPacketTest() throws UnknownHostException {
        this.reserved = 123454321;
        this.targetAddress = (Inet6Address) InetAddress.getByName("2001:db8::aaaa:bbbb:0:0");

        Builder opt = new Builder();
        opt.linkLayerAddress(
                        new byte[]{
                                (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03
                        })
                .correctLengthAtBuild(true);
        this.options.add(opt.build());

        IcmpV6NeighborSolicitationPacket.Builder b = new IcmpV6NeighborSolicitationPacket.Builder();
        b.reserved(reserved).targetAddress(targetAddress).options(options);
        this.packet = b.build();
    }

    @Override
    protected Packet getPacket() {
        return packet;
    }

    @Override
    protected Packet getWholePacket() {
        Inet6Address srcAddr;
        Inet6Address dstAddr;
        try {
            srcAddr = (Inet6Address) InetAddress.getByName("2001:db8::3:2:1");
            dstAddr = (Inet6Address) InetAddress.getByName("2001:db8::3:2:2");
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
        IcmpV6CommonPacket.Builder icmpV6b = new IcmpV6CommonPacket.Builder();
        icmpV6b
                .type(IcmpV6Type.NEIGHBOR_SOLICITATION)
                .code(IcmpV6Code.NO_CODE)
                .srcAddr(srcAddr)
                .dstAddr(dstAddr)
                .payloadBuilder(new SimpleBuilder(packet))
                .correctChecksumAtBuild(true);

        IpV6Packet.Builder ipv6b = new IpV6Packet.Builder();
        ipv6b
                .version(IpVersion.IPV6)
                .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0x12))
                .flowLabel(IpV6SimpleFlowLabel.newInstance(0x12345))
                .nextHeader(IpNumber.ICMPV6)
                .hopLimit((byte) 100)
                .srcAddr(srcAddr)
                .dstAddr(dstAddr)
                .correctLengthAtBuild(true)
                .payloadBuilder(icmpV6b);

        EthernetPacket.Builder eb = new EthernetPacket.Builder();
        eb.dstAddr(MacAddress.getByName("fe:00:00:00:00:02"))
                .srcAddr(MacAddress.getByName("fe:00:00:00:00:01"))
                .type(EtherType.IPV6)
                .payloadBuilder(ipv6b)
                .paddingAtBuild(true);
        return eb.build();
    }

    @Test
    public void testNewPacket() {
        try {
            IcmpV6NeighborSolicitationPacket p =
                    IcmpV6NeighborSolicitationPacket.newPacket(
                            packet.getRawData(), 0, packet.getRawData().length);
            assertEquals(packet, p);
        } catch (IllegalRawDataException e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void testGetHeader() {
        IcmpV6NeighborSolicitationHeader h = packet.getHeader();
        assertEquals(targetAddress, h.getTargetAddress());
        assertEquals(reserved, h.getReserved());
        Iterator<IpV6NeighborDiscoveryOption> iter = h.getOptions().iterator();
        for (IpV6NeighborDiscoveryOption expected : options) {
            IpV6NeighborDiscoveryOption actual = iter.next();
            assertEquals(expected, actual);
        }
    }
}
