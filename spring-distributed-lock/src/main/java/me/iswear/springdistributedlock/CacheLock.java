package me.iswear.springdistributedlock;

import me.iswear.springdistributedlock.exception.LockExpiredException;

public interface CacheLock {

    void lock(String key, CacheLockCallBack callBack) throws LockExpiredException;

    boolean tryLock(String key, CacheLockCallBack callBack) throws LockExpiredException;

    void unLock(String key) throws LockExpiredException;

}
