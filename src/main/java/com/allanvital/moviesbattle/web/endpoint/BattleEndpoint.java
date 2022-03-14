package com.allanvital.moviesbattle.web.endpoint;

import com.allanvital.moviesbattle.infra.ShowInDocumentPage;
import com.allanvital.moviesbattle.web.endpoint.exception.GameIsClosedException;
import com.allanvital.moviesbattle.web.endpoint.integrity.GameAndUserMatchValidator;
import com.allanvital.moviesbattle.web.endpoint.resource.BattleResource;
import com.allanvital.moviesbattle.web.endpoint.resource.GameResource;
import com.allanvital.moviesbattle.web.endpoint.resource.ResponseResource;
import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.service.BattleService;
import com.allanvital.moviesbattle.web.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ShowInDocumentPage
@RestController
@Api(tags = { "Battle" })
@RequestMapping(path = "/games/{game_id}/battles", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class BattleEndpoint {

    private BattleService service;
    private GameService gameService;
    private GameAndUserMatchValidator validator;

    public BattleEndpoint(BattleService service, GameService gameService, GameAndUserMatchValidator validator) {
        this.service = service;
        this.gameService = gameService;
        this.validator = validator;
    }

    @Operation(summary = "Create the next battle",
            tags = {"Battle"},
            responses = {
                    @ApiResponse(description = "Battle is created and displayed. In case that there is a battle without response, return the already created battle", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BattleResource.class))),
                    @ApiResponse(responseCode = "401", description = "User not logged on")})
    @PostMapping
    public ResponseEntity<BattleResource> getNextBattle(@PathVariable("game_id") Integer gameId, @ApiIgnore Authentication authentication) {
        validator.validateGameIsFromUser(gameId, authentication);
        Game game = gameService.findGame(gameId);
        Battle nextBattle = service.getNextBattle(game);
        if (nextBattle == null) {
            throw new GameIsClosedException("Game is closed, so no more new battles can be provided");
        }
        BattleResource resource = new BattleResource(nextBattle);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Operation(summary = "Answer the current open battle",
            tags = {"Battle"},
            responses = {
                    @ApiResponse(description = "Answer the current open battle with the movie id of the player choosing", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BattleResource.class))),
                    @ApiResponse(responseCode = "401", description = "User not logged on")})
    @PatchMapping
    public ResponseEntity<BattleResource> answerBattle(@PathVariable("game_id") Integer gameId, @RequestBody ResponseResource userAnswer, @ApiIgnore Authentication authentication) {
        validator.validateGameIsFromUser(gameId, authentication);
        Game game = gameService.findGame(gameId);
        service.answerCurrentBattle(game, userAnswer.getMovieAnswerId());
        BattleResource resource = new BattleResource(service.getCurrentBattle(game));
        resource.setUserAnswer(userAnswer);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Operation(summary = "Get a battle by id",
            tags = {"Battle"},
            responses = {
                    @ApiResponse(description = "Shows the queried battle", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BattleResource.class))),
                    @ApiResponse(responseCode = "401", description = "User not logged on")})

    @GetMapping("/{battle_id}")
    public ResponseEntity<BattleResource> getCurrentBattle(@PathVariable("game_id") Integer gameId, @PathVariable("battle_id") Integer battleId, @ApiIgnore Authentication authentication) {
        validator.validateGameIsFromUser(gameId, authentication);
        Game game = gameService.findGame(gameId);
        BattleResource resource = new BattleResource(service.getCurrentBattle(game));
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

}
