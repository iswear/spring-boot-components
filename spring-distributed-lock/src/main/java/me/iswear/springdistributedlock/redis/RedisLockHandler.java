package me.iswear.springdistributedlock.redis;

public interface RedisLockHandler {

    /**
     * 获取锁
     * @param key
     * @param lockContent
     * @param timeOut
     * @return
     */
    boolean getLockOfKey(String key, String lockContent, int timeOut);

    /**
     * 维持锁
     * @param key
     * @param timeOut
     * @return
     */
    boolean keepLockOfKey(String key, int timeOut);

    /**
     * 释放锁
     * @param key
     * @return
     */
    boolean releaseLockOfKey(String key);

    /**
     * 获取锁缓存内容
     * @param key
     * @return
     */
    String getLockContentOfKey(String key);

}
