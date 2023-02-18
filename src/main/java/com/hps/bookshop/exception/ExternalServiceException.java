package com.hps.bookshop.exception;

public class ExternalServiceException extends RuntimeException{
    public ExternalServiceException() {
        super();
    }

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
