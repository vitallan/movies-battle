package com.allanvital.moviesbattle.web.model;


import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Battle {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "left_bracket_movie_id", nullable = false)
    private Movie leftBracket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "right_bracket_movie_id", nullable = false)
    private Movie rightBracket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correct_answer_movie_id", nullable = false)
    private Movie correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_answer_movie_id")
    private Movie playerAnswer;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime answeredAt;

    public Battle() {}

    public Battle(Game game, Movie firstMovie, Movie secondMovie) {
        this.game = game;
        this.rightBracket = firstMovie;
        this.leftBracket = secondMovie;
        if (firstMovie.getRating() > secondMovie.getRating()) { //fixme:think about tie rule
            this.correctAnswer = firstMovie;
        } else {
            this.correctAnswer = secondMovie;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Movie getLeftBracket() {
        return leftBracket;
    }

    public void setLeftBracket(Movie leftBracket) {
        this.leftBracket = leftBracket;
    }

    public Movie getRightBracket() {
        return rightBracket;
    }

    public void setRightBracket(Movie rightBracket) {
        this.rightBracket = rightBracket;
    }

    public Movie getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean playerAnsweredCorrectly(){
        if(playerAnswer == null) {
            return false;
        }
        return this.correctAnswer.getId() == this.playerAnswer.getId();
    }

    public void setCorrectAnswer(Movie correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Movie getPlayerAnswer() {
        return playerAnswer;
    }

    public void setPlayerAnswer(Movie playerAnswer) {
        this.playerAnswer = playerAnswer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }

    public void answerBattle(Movie movie) {
        this.playerAnswer = movie;
        this.answeredAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Battle)) return false;
        Battle battle = (Battle) o;
        return id.equals(battle.id) &&
                game.equals(battle.game) &&
                leftBracket.equals(battle.leftBracket) &&
                rightBracket.equals(battle.rightBracket) &&
                correctAnswer.equals(battle.correctAnswer) &&
                playerAnswer.equals(battle.playerAnswer) &&
                Objects.equals(createdAt, battle.createdAt) &&
                Objects.equals(answeredAt, battle.answeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, game, leftBracket, rightBracket, correctAnswer, playerAnswer, createdAt, answeredAt);
    }

    @Override
    public String toString() {
        return "Battle{" +
                "id=" + id +
                ", game=" + game +
                ", leftBracket=" + leftBracket +
                ", rightBracket=" + rightBracket +
                '}';
    }
}
