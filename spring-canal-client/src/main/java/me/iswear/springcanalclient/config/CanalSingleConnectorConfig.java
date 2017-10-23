package me.iswear.springcanalclient.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanalSingleConnectorConfig extends CanalConnectorConfig implements Serializable {

    private static final long serialVersionUID = 1377999884999320870L;

    private IPAddress address;

}
