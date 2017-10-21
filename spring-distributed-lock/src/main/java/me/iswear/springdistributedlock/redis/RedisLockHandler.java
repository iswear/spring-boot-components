package me.iswear.springdistributedlock.redis;

public interface RedisLockHandler {

    boolean getLockOfKey(String key, int timeOut);

    boolean keepLockOfKey(String key, int timeOut);

    boolean releaseLockOfKey(String key);

}
