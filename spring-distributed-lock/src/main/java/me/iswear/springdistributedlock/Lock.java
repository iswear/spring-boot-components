package me.iswear.springdistributedlock;

public interface Lock {

    void Lock();

    boolean tryLock();

    void unLock();

}
