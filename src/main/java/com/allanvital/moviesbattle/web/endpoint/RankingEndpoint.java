package com.allanvital.moviesbattle.web.endpoint;

import com.allanvital.moviesbattle.infra.ShowInDocumentPage;
import com.allanvital.moviesbattle.web.endpoint.resource.GameResource;
import com.allanvital.moviesbattle.web.service.RankingService;
import com.allanvital.moviesbattle.web.service.pojo.UserRanking;
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

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ShowInDocumentPage
@RestController
@Api(tags = { "Ranking" })
@RequestMapping(path = "/ranking", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class RankingEndpoint {

    private RankingService rankingService;

    public RankingEndpoint(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Operation(summary = "Create a new Game",
            tags = {"Game"},
            responses = {
                    @ApiResponse(description = "Game is created", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GameResource.class))),
                    @ApiResponse(responseCode = "401", description = "User not logged on")})
    @GetMapping
    public ResponseEntity<List<UserRanking>> getRanking() {
        List<UserRanking> users = rankingService.getRanking();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
