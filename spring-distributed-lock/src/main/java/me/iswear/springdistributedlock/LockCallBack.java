package me.iswear.springdistributedlock;

/**
 * Created by iswear on 2017/10/22.
 */
public interface LockCallBack {


    void onLockExpiredException(Throwable throwable);

    void onOtherException(Throwable throwable);

}
