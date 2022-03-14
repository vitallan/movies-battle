package com.allanvital.moviesbattle.web.service.pojo;

import java.util.Objects;

public class UserRanking implements Comparable<UserRanking>{

    private String username;
    private Double scoreboard;

    public UserRanking(String username, Double scoreboard) {
        this.username = username;
        this.scoreboard = scoreboard;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Double scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRanking)) return false;
        UserRanking that = (UserRanking) o;
        return Objects.equals(username, that.username) && Objects.equals(scoreboard, that.scoreboard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, scoreboard);
    }

    @Override
    public int compareTo(UserRanking o) {
        if(scoreboard > o.scoreboard) {
            return 1;
        } else if (scoreboard == o.scoreboard) {
            return 0;
        } else {
            return -1;
        }
    }
}
