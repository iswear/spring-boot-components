package me.iswear.springdistributedlock.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.iswear.springdistributedlock.Lock;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Data
public class RedisReentrantLockConfig implements Serializable {

    private static final long serialVersionUID = -349252563748015280L;

    private int corePoolSize;

    private int maxPoolSize;

    private int lockTimeOut;

    private int lockKeepInterval;

}
