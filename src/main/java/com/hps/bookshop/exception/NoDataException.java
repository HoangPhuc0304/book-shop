package com.hps.bookshop.exception;

public class NoDataException extends RuntimeException {
    public NoDataException() {
        super();
    }

    public NoDataException(String message) {
        super(message);
    }

    public NoDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
