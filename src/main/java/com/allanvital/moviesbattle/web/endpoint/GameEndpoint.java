package com.allanvital.moviesbattle.web.endpoint;

import com.allanvital.moviesbattle.web.endpoint.resource.GameResource;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/games", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class GameEndpoint {

    private GameService service;

    public GameEndpoint(GameService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GameResource> startNewGame() {
        GameResource game = new GameResource(service.startNewGame());
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GameResource> closeGame(@PathVariable("id") Integer id) {
        Game game = service.closeGame(id);
        if (game != null) {
            return new ResponseEntity<>(new GameResource(game), HttpStatus.OK);
        } else {
            throw new RuntimeException("erro ao tentar fechar jogo");
        }
    }

}
