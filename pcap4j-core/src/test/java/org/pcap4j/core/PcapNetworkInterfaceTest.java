package org.pcap4j.core;

import org.junit.jupiter.api.Test;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("javadoc")
public class PcapNetworkInterfaceTest {

    private static final Logger logger = LoggerFactory.getLogger(PcapNetworkInterfaceTest.class);

    @Test
    public void testOpenLive() throws Exception {
        PcapHandle handle;
        try {
            handle = Pcaps.findAllDevs().get(0).openLive(55555, PromiscuousMode.PROMISCUOUS, 100);
        } catch (IndexOutOfBoundsException e) {
            return;
        } catch (PcapNativeException e) {
            assertTrue(
                    e.getMessage().contains("You don't have permission to capture on that device"),
                    "The exception should complain about permission to capture.");
            return;
        }

        assertNotNull(handle);
        assertTrue(handle.isOpen());

        logger.info(handle.toString());
    }
}
