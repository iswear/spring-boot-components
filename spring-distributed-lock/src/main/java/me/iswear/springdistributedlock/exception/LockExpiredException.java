package me.iswear.springdistributedlock.exception;

/**
 * Created by iswear on 2017/10/22.
 */
public class LockExpiredException extends Exception {

    public LockExpiredException() {
        super();
    }

    public LockExpiredException(String message) {
        super(message);
    }

    public LockExpiredException(Throwable throwable) {
        super(throwable);
    }

    public LockExpiredException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
