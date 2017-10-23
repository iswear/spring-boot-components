package me.iswear.springcanalclient.config;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CanalClusterConnectorConfig extends CanalConnectorConfig implements Serializable {

    private static final long serialVersionUID = 7501558915461381323L;

    private List<IPAddress> addresses;

}
