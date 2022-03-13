package com.allanvital.moviesbattle.security.dto;

public class AuthenticationTokenResponse {

    private String token;

    public AuthenticationTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
