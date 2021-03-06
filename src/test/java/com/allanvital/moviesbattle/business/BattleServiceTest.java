package com.allanvital.moviesbattle.business;

import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.model.Movie;
import com.allanvital.moviesbattle.web.model.User;
import com.allanvital.moviesbattle.web.service.BattleService;
import com.allanvital.moviesbattle.web.service.GameService;
import com.allanvital.moviesbattle.web.service.exception.ApplicationInInvalidStateException;
import com.allanvital.moviesbattle.web.service.exception.NoMoreValidBattleMatchesException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BattleServiceTest {

    @Autowired
    private BattleService service;

    @Autowired
    private GameService gameService;

    private Game game;

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldCreateNewBattle_WhenNoBattlesArePersistedAndDataIsValid() {
        Battle battle = service.getNextBattle(game);

        assertNotNull(battle.getLeftBracket(), "There should be a left bracket movie");
        assertNotNull(battle.getRightBracket(), "There should be a right bracket movie");
        assertNotNull(battle.getGame(), "There should be a game assigned");
        assertEquals(1, battle.getId(), "There should be an ID for the battle");
        assertEquals(1, (int) battle.getCorrectAnswer().getId(), "The correct answer is the movie with ID 1");
        assertNull(battle.getPlayerAnswer(), "There should be no player answer");
    }

    @Test
    @Sql(scripts = {"/data/three-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldNotCreateNewBattle_WhenThereIsOneBattleWithoutAnswer() {
        Battle firstBattle = service.getNextBattle(game);
        Battle secondBattle = service.getNextBattle(game);

        assertEquals(firstBattle, secondBattle, "Should not create a new battle when there is a battle without answer");
    }

    @Test
    @Sql(scripts = {"/data/single-movie.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldNotCreateBattle_WhenThereIsNotEnoughMovies() {
        ApplicationInInvalidStateException exception = assertThrows(ApplicationInInvalidStateException.class, () -> {
            service.getNextBattle(game);
        });
        String expectedMessage = "There are fewer than 2 movies in database, therefore there are not possible battles";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldRaiseException_WhenThereIsNoMoreValidBattles() {
        Battle firstBattle = service.getNextBattle(game);
        service.answerCurrentBattle(game, firstBattle.getLeftBracket());
        NoMoreValidBattleMatchesException exception = assertThrows(NoMoreValidBattleMatchesException.class, () -> {
           service.getNextBattle(game);
        });
        String expectedMessage = "There are no more possible movies to create a new battle for this game";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @Sql(scripts = {"/data/three-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldCreateNewBattle_WhenTheLastBattleWasAnswered() {
        Battle firstBattle = service.getNextBattle(game);
        service.answerCurrentBattle(game, firstBattle.getLeftBracket());
        Battle secondBattle = service.getNextBattle(game);

        assertNotEquals(firstBattle, secondBattle, "There must be a new battle after the first one is answered");
    }

    @Test
    @Sql(scripts = {"/data/two-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldPersistAnswer_WhenBattleIsAnswered() {
        Battle firstBattle = service.getNextBattle(game);
        service.answerCurrentBattle(game, firstBattle.getLeftBracket());
        Battle battle = service.getCurrentBattle(game);

        assertEquals(battle.getPlayerAnswer(), firstBattle.getLeftBracket(), "The answer persisted should be the same as the given by the player");
        assertTrue(LocalDateTime.now().isAfter(battle.getAnsweredAt()), "There should be a date for the answer, and it must be earlier than now"); //fixme: create a mockable provider for a datetime getter
    }

    @Test
    @Sql(scripts = {"/data/five-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/data/tear-down.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldNotCreateNewBattle_WhenThreeErrorsHaveAlreadyBeenAnswered() {
        Battle firstBattle = service.getNextBattle(game);
        answerWrong(firstBattle);
        Battle secondBattle = service.getNextBattle(game);
        answerWrong(secondBattle);
        Battle thirdBattle = service.getNextBattle(game);
        answerWrong(thirdBattle);
        Battle noBattle = service.getNextBattle(game);

        assertNull(noBattle);
    }

    @BeforeEach
    private void createMockGame() {
        this.game = gameService.startNewGame("placeholder");
    }

    private void answerWrong(Battle battle) {
        Movie leftBracket = battle.getLeftBracket();
        Movie rightBracket = battle.getRightBracket();
        if(leftBracket.getRating() > rightBracket.getRating()) {
            service.answerCurrentBattle(battle.getGame(), rightBracket);
        } else {
            service.answerCurrentBattle(battle.getGame(), leftBracket);
        }
    }

}
