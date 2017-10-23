package me.iswear.springdistributedlock.redis.impl;

import me.iswear.springdistributedlock.redis.RedisLockHandler;

public abstract class AbstractRedisLockHandler implements RedisLockHandler {

    @Override
    public boolean getLockOfKey(String key, String lockContent, int timeOut) {
        Long result = this.setNx(key, lockContent);
        if (result == 1) {
            this.expire(key, timeOut);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean keepLockOfKey(String key, int timeOut) {
        return this.expire(key, timeOut) == 1;
    }

    @Override
    public boolean releaseLockOfKey(String key) {
        return this.del(key) == 1;
    }

    @Override
    public String getLockContentOfKey(String key) {
        return this.get(key);
    }


    public abstract Long setNx(String key, String value);

    public abstract Long expire(String key, int seconds);

    public abstract Long del(String key);

    public abstract String get(String key);

}
