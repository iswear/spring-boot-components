package me.iswear.springdistributedlock;

import me.iswear.springdistributedlock.exception.LockExpiredException;

public interface Lock {

    void lock(String key, LockCallBack callBack) throws LockExpiredException;

    boolean tryLock(String key, LockCallBack callBack) throws LockExpiredException;

    void unLock(String key) throws LockExpiredException;

}
