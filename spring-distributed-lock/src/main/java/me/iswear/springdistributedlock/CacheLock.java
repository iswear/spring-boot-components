package me.iswear.springdistributedlock;

import me.iswear.springdistributedlock.exception.LockExpiredException;

public interface CacheLock {

    /**
     * 阻塞获取锁
     * @param key
     * @param callBack
     * @throws LockExpiredException
     */
    void lock(String key, CacheLockCallBack callBack) throws LockExpiredException;

    /**
     * 不阻塞尝试获取锁，
     * @param key
     * @param callBack
     * @return 成功true，失败false
     * @throws LockExpiredException
     */
    boolean tryLock(String key, CacheLockCallBack callBack) throws LockExpiredException;

    /**
     * 解锁
     * @param key
     * @throws LockExpiredException
     */
    void unLock(String key) throws LockExpiredException;

}
