package me.iswear.springdistributedlock;

public interface Lock {

    void Lock(String key);

    boolean tryLock(String key);

    void unLock(String key);

}
