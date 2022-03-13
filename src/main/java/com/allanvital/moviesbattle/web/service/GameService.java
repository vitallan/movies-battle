package com.allanvital.moviesbattle.web.service;

import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GameService {

    private GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    public Game startNewGame() {
        return repository.save(new Game());
    }

    public Game findGame(Integer gameId) {
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            return null;
        }
        return optionalGame.get();
    }

    public Game closeGame(Integer gameId) {
        Game game = this.findGame(gameId);
        if (game == null) {
            return null;
        }
        if(game.getClosedAt() != null) {
            return game;
        }
        game.setClosedAt(LocalDateTime.now());
        return repository.save(game);
    }

}
