package com.allanvital.moviesbattle.dto;

import java.time.LocalDateTime;

public class GameDTO {

    private Integer id;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    public GameDTO() {}

    public GameDTO(Integer id, LocalDateTime createdAt, LocalDateTime closedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
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
