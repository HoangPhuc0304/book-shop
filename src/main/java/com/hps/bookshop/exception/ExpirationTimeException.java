package com.hps.bookshop.exception;

public class ExpirationTimeException extends RuntimeException {
    public ExpirationTimeException() {
        super();
    }

    public ExpirationTimeException(String message) {
        super(message);
    }

    public ExpirationTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
