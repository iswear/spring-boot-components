package me.iswear.springdistributedlock.redis;

public abstract class AbstractRedisLockHandler implements RedisLockHandler {

    @Override
    public boolean getLockOfKey(String key, int timeOut) {
        Long result = this.setNx(key);
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

    public abstract Long setNx(String key);

    public abstract Long expire(String key, int seconds);

    public abstract Long del(String key);

}
