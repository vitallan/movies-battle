package com.allanvital.moviesbattle.web.service;

import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public Game startNewGame() {
        return repository.save(new Game());
    }

}
