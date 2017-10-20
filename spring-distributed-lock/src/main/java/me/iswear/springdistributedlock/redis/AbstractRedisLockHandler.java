package me.iswear.springdistributedlock.redis;

import redis.clients.jedis.JedisCommands;

public abstract class AbstractRedisLockHandler implements RedisLockHandler {

    @Override
    public boolean getLockOfKey(String key, long timeOut) {
        Long result = this.setNx(key);
        return
    }

    @Override
    public boolean keepLockOfKey(String key, long timeOut) {
        return false;
    }

    public abstract Long setNx(String key);

}
