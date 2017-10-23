package me.iswear.springcanalclient.config;


import lombok.Data;

import java.io.Serializable;

/**
 * @author hujianbing
 */
@Data
public class CanalClientConfig implements Serializable {

    private static final long serialVersionUID = -4426826074098888533L;

    private int poolCoreSize;

    private int poolMaxSize;

    private int batchSize;

}
