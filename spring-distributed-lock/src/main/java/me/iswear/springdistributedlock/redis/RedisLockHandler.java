package me.iswear.springdistributedlock.redis;

public interface RedisLockHandler {

    boolean getLockOfKey(String key, long timeOut);

    boolean keepLockOfKey(String key, long timeOut);

}
