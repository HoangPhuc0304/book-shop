package com.hps.bookshop.exception;

public class NotMatchException extends RuntimeException {
    public NotMatchException() {
        super();
    }

    public NotMatchException(String message) {
        super(message);
    }

    public NotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
