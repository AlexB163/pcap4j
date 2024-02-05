module org.pcap4j.core {
  exports org.pcap4j.core;
  exports org.pcap4j.packet;
  exports org.pcap4j.packet.factory;
  exports org.pcap4j.packet.namednumber;
  exports org.pcap4j.packet.constant;
  exports org.pcap4j.util;

  opens org.pcap4j.packet;
  opens org.pcap4j.packet.namednumber;

  requires java.sql;
  requires java.base;
  requires com.sun.jna;
  requires transitive org.slf4j;

  uses org.pcap4j.packet.factory.PacketFactoryBinderProvider;
}
