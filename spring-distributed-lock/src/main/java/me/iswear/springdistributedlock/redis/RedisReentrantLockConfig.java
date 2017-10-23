package me.iswear.springdistributedlock.redis;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisReentrantLockConfig implements Serializable {

    private static final long serialVersionUID = -349252563748015280L;

    private int corePoolSize;

    private int maxPoolSize;

    private int lockTimeOut;

    private int lockKeepInterval;

}
