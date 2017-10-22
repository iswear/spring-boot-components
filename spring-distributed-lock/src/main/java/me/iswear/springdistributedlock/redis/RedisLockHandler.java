package me.iswear.springdistributedlock.redis;

public interface RedisLockHandler {

    boolean getLockOfKey(String key, String lockContent, int timeOut);

    boolean keepLockOfKey(String key, int timeOut);

    boolean releaseLockOfKey(String key);

    String getLockContentOfKey(String key);

}
