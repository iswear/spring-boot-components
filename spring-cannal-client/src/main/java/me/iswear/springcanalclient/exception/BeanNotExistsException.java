package me.iswear.springcanalclient.exception;

public class BeanNotExistsException extends RuntimeException {

    public BeanNotExistsException() {
        super();
    }

    public BeanNotExistsException(String message) {
        super(message);
    }

    public BeanNotExistsException(Throwable throwable) {
        super(throwable);
    }

    public BeanNotExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
