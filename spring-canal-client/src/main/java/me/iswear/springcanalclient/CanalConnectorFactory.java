package me.iswear.springcanalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import me.iswear.springcanalclient.config.CanalClusterConnectorConfig;
import me.iswear.springcanalclient.config.CanalSingleConnectorConfig;
import me.iswear.springcanalclient.config.CanalZkClusterConnectorConfig;
import org.springframework.util.CollectionUtils;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

public class CanalConnectorFactory {

    public static CanalConnector createCanalConnector(CanalSingleConnectorConfig config) {
        return CanalConnectors.newSingleConnector(
                config.getAddress().toSocketAddress(),
                config.getDestination(),
                config.getUsername(),
                config.getPassword()
        );
    }

    public static CanalConnector createCanalConnector(CanalClusterConnectorConfig config) {
        List<SocketAddress> addresses = new LinkedList<>();
        if (!CollectionUtils.isEmpty(config.getAddresses())) {
            config.getAddresses().forEach((ipAddress) -> addresses.add(ipAddress.toSocketAddress()));
        }
        return CanalConnectors.newClusterConnector(
                addresses,
                config.getDestination(),
                config.getUsername(),
                config.getPassword()
        );
    }

    public static CanalConnector createCanalConnector(CanalZkClusterConnectorConfig config) {
        return CanalConnectors.newClusterConnector(
                config.getZkServers(),
                config.getDestination(),
                config.getUsername(),
                config.getPassword()
        );
    }

}
