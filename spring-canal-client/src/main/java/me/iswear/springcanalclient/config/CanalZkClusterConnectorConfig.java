package me.iswear.springcanalclient.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanalZkClusterConnectorConfig extends CanalConnectorConfig implements Serializable {

    private static final long serialVersionUID = -3605503179175970454L;

    private String zkServers;

}
