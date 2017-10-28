package me.iswear.springdistributedlock.redis.impl;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedisReentrantLockConfig implements Serializable {

    private static final long serialVersionUID = -349252563748015280L;

    /**
     * 维持锁过期时间核心线程数量
     */
    private int corePoolSize;

    /**
     * 维持锁过期时间最大线程数量
     */
    private int maxPoolSize;

    /**
     * 锁超时时间
     */
    private int lockTimeOut;

    /**
     * 维持锁过期时间循环周期
     */
    private int lockKeepInterval;

}
