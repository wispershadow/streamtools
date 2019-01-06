package io.wispershadow.stream.service.ext.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.NullLogger;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.communication.CommunicationSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EmbedIgniteBuilder {
    private static final int DEFAULT_PORT_RANGE = 10;
    private static final Map<String, Ignite> embedIgniteInstancesByXml = new HashMap<>();
    private static final Map<String, Ignite> embedIgniteInstancesByProp = new HashMap<>();

    private static DiscoverySpi buildDiscoverySpi(int port, int portRange) {
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        discoverySpi.setLocalPort(port);

        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Collections.singletonList(String.format("localhost:%d..%d", port, port + portRange)));

        discoverySpi.setIpFinder(ipFinder);
        return discoverySpi;
    }

    private static CommunicationSpi buildCommunicationSpi(int port, int portRange) {
        TcpCommunicationSpi communicationSpi = new TcpCommunicationSpi();
        communicationSpi.setLocalPort(port);
        communicationSpi.setLocalPortRange(portRange);
        return communicationSpi;
    }

    public synchronized static Ignite buildIgnite(int discoveryPort, int communicationPort, boolean debug) {
        String key = String.join("_", String.valueOf(discoveryPort),
                String.valueOf(DEFAULT_PORT_RANGE), String.valueOf(communicationPort),
                String.valueOf(DEFAULT_PORT_RANGE), String.valueOf(debug));
        Ignite instance = embedIgniteInstancesByProp.get(key);
        if (instance == null) {
            IgniteConfiguration configuration = new IgniteConfiguration();
            configuration.setClientMode(false);
            configuration.setIgniteInstanceName("EmbeddedIgnite");
            if (debug) {
                configuration.setGridLogger(new Slf4jLogger());
            }
            else {
                configuration.setGridLogger(new NullLogger());
            }
            configuration.setDiscoverySpi(buildDiscoverySpi(discoveryPort, DEFAULT_PORT_RANGE));
            configuration.setCommunicationSpi(buildCommunicationSpi(communicationPort, DEFAULT_PORT_RANGE));
            instance = Ignition.start(configuration);
            embedIgniteInstancesByProp.put(key, instance);
        }
        return instance;
    }

    public synchronized static Ignite buildIgnite(String xmlConfigFile) {
        Ignite instance = embedIgniteInstancesByXml.get(xmlConfigFile);
        if (instance == null) {
            Ignition.setClientMode(false);
            instance = Ignition.start(xmlConfigFile);
            embedIgniteInstancesByXml.put(xmlConfigFile, instance);
        }
        return instance;
    }
}
