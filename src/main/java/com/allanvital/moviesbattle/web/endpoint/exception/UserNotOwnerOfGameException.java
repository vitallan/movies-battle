package com.allanvital.moviesbattle.web.endpoint.exception;

public class UserNotOwnerOfGameException extends RuntimeException {

    public UserNotOwnerOfGameException(String message) {
        super(message);
    }

}
