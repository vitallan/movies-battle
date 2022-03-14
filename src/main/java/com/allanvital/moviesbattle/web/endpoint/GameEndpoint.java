package com.allanvital.moviesbattle.web.endpoint;

import com.allanvital.moviesbattle.infra.ShowInDocumentPage;
import com.allanvital.moviesbattle.web.endpoint.exception.GameNotFoundException;
import com.allanvital.moviesbattle.web.endpoint.integrity.GameAndUserMatchValidator;
import com.allanvital.moviesbattle.web.endpoint.resource.GameResource;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.service.GameService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ShowInDocumentPage
@RestController
@Api(tags = { "Game" })
@RequestMapping(path = "/games", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class GameEndpoint {

    private GameService service;
    private GameAndUserMatchValidator validator;

    public GameEndpoint(GameService service, GameAndUserMatchValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @Operation(summary = "Create a new Game",
            tags = {"Game"},
            responses = {
                    @ApiResponse(description = "Game is created", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameResource.class))),
                    @ApiResponse(responseCode = "401", description = "User not logged on")})
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<GameResource> startNewGame(@ApiIgnore Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();

        GameResource game = new GameResource(service.startNewGame(user.getUsername()));
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @Operation(summary = "Closes a created game",
            tags = {"Game"},
            responses = {
                    @ApiResponse(description = "Game is created", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameResource.class, hidden = false))),
                    @ApiResponse(responseCode = "404", description = "Game provided does not exists"),
                    @ApiResponse(responseCode = "401", description = "User not logged on")})
    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping("/{id}")
    public ResponseEntity<GameResource> closeGame(@PathVariable("id") Integer id, @ApiIgnore Authentication authentication) {
        validator.validateGameIsFromUser(id, authentication);
        Game game = service.closeGame(id);
        if (game != null) {
            return new ResponseEntity<>(new GameResource(game), HttpStatus.OK);
        } else {
            throw new GameNotFoundException("Game was not found");
        }
    }

}
