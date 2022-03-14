package com.allanvital.moviesbattle.web.endpoint.resource;

import com.allanvital.moviesbattle.web.model.Battle;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleResource {

    private Integer id;
    private MovieResource leftBracket;
    private MovieResource rightBracket;

    @ApiModelProperty(
            required = false,
            hidden = true
    )
    private ResponseResource userAnswer;

    public BattleResource() {}

    public BattleResource(Battle battle) {
        this.id = battle.getId();
        this.leftBracket = new MovieResource(battle.getLeftBracket());
        this.rightBracket = new MovieResource(battle.getRightBracket());
        if(battle.getPlayerAnswer() != null) {
            this.userAnswer = new ResponseResource(battle.getPlayerAnswer().getId());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovieResource getLeftBracket() {
        return leftBracket;
    }

    public void setLeftBracket(MovieResource leftBracket) {
        this.leftBracket = leftBracket;
    }

    public MovieResource getRightBracket() {
        return rightBracket;
    }

    public void setRightBracket(MovieResource rightBracket) {
        this.rightBracket = rightBracket;
    }

    public ResponseResource getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(ResponseResource userAnswer) {
        this.userAnswer = userAnswer;
    }
}
