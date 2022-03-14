package com.allanvital.moviesbattle.web.endpoint.resource;

import com.allanvital.moviesbattle.web.model.Game;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResource {

    private Integer id;
    private LocalDateTime createdAt;

    @ApiModelProperty(
            required = false,
            hidden = true
    )
    private LocalDateTime closedAt;

    public GameResource(Game game) {
        this.id = game.getId();
        this.createdAt = game.getCreatedAt();
        this.closedAt = game.getClosedAt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
