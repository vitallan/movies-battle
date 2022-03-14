package com.allanvital.moviesbattle.web.service;

import com.allanvital.moviesbattle.utils.MathUtils;
import com.allanvital.moviesbattle.utils.exception.MaximumValueTooLowException;
import com.allanvital.moviesbattle.utils.exception.TooManyTriesToFindPairException;
import com.allanvital.moviesbattle.web.endpoint.exception.BattleNotFoundException;
import com.allanvital.moviesbattle.web.endpoint.exception.GameNotFoundException;
import com.allanvital.moviesbattle.web.endpoint.exception.InvalidIdAnswerException;
import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.model.Movie;
import com.allanvital.moviesbattle.web.repository.BattleRepository;
import com.allanvital.moviesbattle.web.repository.GameRepository;
import com.allanvital.moviesbattle.web.repository.MovieRepository;
import com.allanvital.moviesbattle.web.service.exception.ApplicationInInvalidStateException;
import com.allanvital.moviesbattle.web.service.exception.BattleNotCreatedException;
import com.allanvital.moviesbattle.web.service.exception.NoMoreValidBattleMatchesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class BattleService {

    private static final Integer MAXIMUM_ERRORS_PER_GAME = 3;
    private final Logger log = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository repository;
    private final MovieRepository movieRepository;
    private final GameRepository gameRepository;

    public BattleService(BattleRepository repository, MovieRepository movieRepository, GameRepository gameRepository) {
        this.repository = repository;
        this.movieRepository = movieRepository;
        this.gameRepository = gameRepository;
    }

    public void answerCurrentBattle(Game game, Movie answer) {
        Battle battle = repository.findByGameAndPlayerAnswerIsNull(game);
        battle.answerBattle(answer);
        repository.save(battle);
        Integer quantityOfWrongAnswers = repository.countWrongAnswersInGame(game);
        if(quantityOfWrongAnswers >= MAXIMUM_ERRORS_PER_GAME) {
            log.info("The maximum number of errors have been answered. Closing game {}", game);
            game.setClosedAt(LocalDateTime.now());
            gameRepository.save(game);
        }
    }

    public void answerCurrentBattle(Game game, Integer answerId) {
        Battle battle = repository.findByGameAndPlayerAnswerIsNull(game);
        Movie answer = null;
        if(battle.getLeftBracket().getId().equals(answerId)) {
            answer = battle.getLeftBracket();
        } else if(battle.getRightBracket().getId().equals(answerId)) {
            answer = battle.getRightBracket();
        } else {
            throw new InvalidIdAnswerException("The answer provided is not a valid option of movie id");
        }
        this.answerCurrentBattle(game, answer);
    }

    public Battle getCurrentBattle(Game game) {
        return repository.findFirstByGameOrderByCreatedAtDesc(game);
    }

    public Battle getNextBattle(Game game) {
        if (game.isClosed()) {
            log.info("This game is closed, so no new battles will be provided {}", game);
            return null;
        }
        Integer quantityOfWrongAnswers = repository.countWrongAnswersInGame(game);
        if(quantityOfWrongAnswers >= MAXIMUM_ERRORS_PER_GAME) {
            log.info("The maximum number of errors have been answered. No new battles will be started in game {}", game);
            return null;
        }
        Battle battle = repository.findByGameAndPlayerAnswerIsNull(game);
        if (battle != null) {
            log.info("Found open battle for game " + game);
            return battle;
        }
        try {
            return createNewBattle(game);
        } catch (MaximumValueTooLowException e) {
            log.error("Application is in invalid state: not enough movies in database to create a battle");
            throw new ApplicationInInvalidStateException("There are fewer than 2 movies in database, therefore there are not possible battles", e);
        } catch (TooManyTriesToFindPairException e) {
            String message = "Due to random imprevisibility, the application was not able to find a valid battle";
            log.error(message);
            throw new BattleNotCreatedException(message, e);
        }
    }

    private Battle createNewBattle(Game game) throws MaximumValueTooLowException {
        log.info("Creating new battle for game {}", game);
        long totalMovies = movieRepository.count();
        List<Pair<Integer, Integer>> triedPairs = new LinkedList<>();
        Movie firstMovie = null;
        Movie secondMovie = null;

        int maximumNumberOfPossiblePairs = (int) (totalMovies * (totalMovies - 1) / 2); //https://stackoverflow.com/questions/18859430/how-do-i-get-the-total-number-of-unique-pairs-of-a-set-in-the-database

        do { //random, cause sequential would be boring
            Pair<Integer, Integer> idPair = MathUtils.findTwoDifferentRandomIntegers(totalMovies);

            log.debug("Trying pair of pageIndex {} of ids for new battle in game {}", idPair, game);
            if (hasPairBeenTried(idPair, triedPairs)) { //to avoid unnecessary db hits
                log.debug("Pair of pageIndex {} already tried, continuing...", idPair);
                continue;
            }
            firstMovie = movieRepository.findMovieInIndex(idPair.getFirst());
            secondMovie = movieRepository.findMovieInIndex(idPair.getSecond());
            if(battleAlreadyPlayed(game, firstMovie, secondMovie)) {
                log.debug("Battle between pair of pageIndex {} already occurred, continuing...", idPair);
                triedPairs.add(idPair);
                firstMovie = null;
                secondMovie = null;
            } else {
                log.debug("Battle between movies {} and {} is valid, creating battle", firstMovie, secondMovie);
                break;
            }
        } while(maximumNumberOfPossiblePairs > triedPairs.size());
        if (firstMovie == null || secondMovie == null) {
            throw new NoMoreValidBattleMatchesException("There are no more possible movies to create a new battle for this game");
        }
        return this.persistBattle(game, firstMovie, secondMovie);
    }

    private Battle persistBattle(Game game, Movie firstMovie, Movie secondMovie) {
        Battle battle = new Battle(game, firstMovie, secondMovie);
        return repository.save(battle);
    }

    private boolean battleAlreadyPlayed(Game game, Movie firstMovie, Movie secondMovie) {
        return repository.findByGameAndLeftBracketAndRightBracket(game, firstMovie, secondMovie) != null ||
                repository.findByGameAndLeftBracketAndRightBracket(game, secondMovie, firstMovie) != null;
    }

    private boolean hasPairBeenTried(Pair<Integer, Integer> pair, List<Pair<Integer, Integer>> pairList) {
        return pairList.contains(Pair.of(pair.getFirst(), pair.getSecond())) || pairList.contains(Pair.of(pair.getSecond(), pair.getFirst()));
     }

    public Battle getBattleFromGame(Integer gameId, Integer battleId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if(game.isEmpty()) {
            throw new GameNotFoundException("Game with id " + gameId + " was not found");
        }
        Optional<Battle> battle = repository.findById(battleId);
        if(battle.isEmpty()) {
            throw new BattleNotFoundException("Battle with id " + battleId + " was not found");
        }
        return battle.get();
    }
}
