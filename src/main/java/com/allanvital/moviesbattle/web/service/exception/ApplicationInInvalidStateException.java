package com.allanvital.moviesbattle.web.service.exception;

public class ApplicationInInvalidStateException extends RuntimeException {

    public ApplicationInInvalidStateException(String message, Throwable err) {
        super(message, err);
    }

}
