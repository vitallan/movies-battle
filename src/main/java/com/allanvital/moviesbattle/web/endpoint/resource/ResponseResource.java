package com.allanvital.moviesbattle.web.endpoint.resource;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResource {

    private Integer movieAnswerId;

    public ResponseResource() {

    }

    public ResponseResource(Integer movieAnswerId) {
        this.movieAnswerId = movieAnswerId;
    }

    public Integer getMovieAnswerId() {
        return movieAnswerId;
    }

    public void setMovieAnswerId(Integer movieAnswerId) {
        this.movieAnswerId = movieAnswerId;
    }
}
