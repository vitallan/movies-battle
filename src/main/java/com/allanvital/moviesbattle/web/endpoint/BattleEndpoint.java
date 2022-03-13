package com.allanvital.moviesbattle.web.endpoint;

import com.allanvital.moviesbattle.web.endpoint.exception.GameIsClosedException;
import com.allanvital.moviesbattle.web.endpoint.resource.BattleResource;
import com.allanvital.moviesbattle.web.endpoint.resource.ResponseResource;
import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.service.BattleService;
import com.allanvital.moviesbattle.web.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/games/{game_id}/battles", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class BattleEndpoint {

    private BattleService service;
    private GameService gameService;

    public BattleEndpoint(BattleService service, GameService gameService) {
        this.service = service;
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<BattleResource> getNextBattle(@PathVariable("game_id") Integer gameId) {
        Game game = gameService.findGame(gameId);
        Battle nextBattle = service.getNextBattle(game);
        if (nextBattle == null) {
            throw new GameIsClosedException("Game is closed, so no more new battles can be provided");
        }
        BattleResource resource = new BattleResource(nextBattle);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<BattleResource> answerBattle(@PathVariable("game_id") Integer gameId, @RequestBody ResponseResource userAnswer) {
        Game game = gameService.findGame(gameId);
        service.answerCurrentBattle(game, userAnswer.getMovieAnswerId());
        BattleResource resource = new BattleResource(service.getCurrentBattle(game));
        resource.setUserAnswer(userAnswer);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping("/{battle_id}")
    public ResponseEntity<BattleResource> getCurrentBattle(@PathVariable("game_id") Integer gameId, @PathVariable("battle_id") Integer battleId) {
        Game game = gameService.findGame(gameId);
        BattleResource resource = new BattleResource(service.getCurrentBattle(game));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

}
