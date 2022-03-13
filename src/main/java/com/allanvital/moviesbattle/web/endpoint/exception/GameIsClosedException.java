package com.allanvital.moviesbattle.web.endpoint.exception;

public class GameIsClosedException extends RuntimeException {

    public GameIsClosedException(String message) {
        super(message);
    }

    public GameIsClosedException(String message, Throwable err) {
        super(message, err);
    }

}
