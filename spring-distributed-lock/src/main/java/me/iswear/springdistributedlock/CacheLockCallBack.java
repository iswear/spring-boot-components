package me.iswear.springdistributedlock;

/**
 * Created by iswear on 2017/10/22.
 */
public interface CacheLockCallBack {

    /**
     * 锁被意外删除或意外丢失时回调
     * @param throwable
     */
    void onLockExpiredException(Throwable throwable);

    /**
     * 其他异常情况回调
     * @param throwable
     */
    void onOtherException(Throwable throwable);

}
