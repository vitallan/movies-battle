package com.allanvital.moviesbattle.web.endpoint.resource;

import com.allanvital.moviesbattle.web.model.Movie;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovieResource {

    private Integer id;
    private String title;

    public MovieResource() {}

    public MovieResource(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
