package com.allanvital.moviesbattle.web.endpoint.resource;

public class ExceptionResource {

    private String message;

    public ExceptionResource(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
