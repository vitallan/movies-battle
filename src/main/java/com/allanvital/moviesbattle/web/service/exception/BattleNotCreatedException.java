package com.allanvital.moviesbattle.web.service.exception;

public class BattleNotCreatedException extends RuntimeException{

    public BattleNotCreatedException(String message, Throwable err) {
        super(message, err);
    }

}
