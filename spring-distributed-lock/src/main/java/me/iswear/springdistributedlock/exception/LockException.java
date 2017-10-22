package me.iswear.springdistributedlock.exception;

/**
 * Created by iswear on 2017/10/22.
 */
public class LockException extends RuntimeException {

    public LockException() {
        super();
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable throwable) {
        super(throwable);
    }

    public LockException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
