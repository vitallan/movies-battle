package com.allanvital.moviesbattle.web.service;

import com.allanvital.moviesbattle.utils.MathUtils;
import com.allanvital.moviesbattle.utils.exception.MaximumValueTooLowException;
import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.model.Movie;
import com.allanvital.moviesbattle.web.repository.BattleRepository;
import com.allanvital.moviesbattle.web.repository.MovieRepository;
import com.allanvital.moviesbattle.web.service.exception.ApplicationInInvalidStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class BattleService {

    private Logger log = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository repository;
    private final MovieRepository movieRepository;

    public BattleService(BattleRepository repository, MovieRepository movieRepository) {
        this.repository = repository;
        this.movieRepository = movieRepository;
    }

    public void answerCurrentBattle(Game game, Movie answer) {
        Battle battle = repository.findByGameAndPlayerAnswerIsNull(game);
        battle.setPlayerAnswer(answer);
        battle.setAnsweredAt(LocalDateTime.now());
        repository.save(battle);
    }

    public Battle getCurrentBattle(Game game) {
        return repository.findByGameAndPlayerAnswerIsNull(game);
    }

    public Battle getNextBattle(Game game) {
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
        }
    }

    private Battle createNewBattle(Game game) throws MaximumValueTooLowException {
        log.info("Creating new battle for game {}", game);
        Battle battle = new Battle();
        long totalMovies = movieRepository.count();
        List<Pair<Integer, Integer>> triedPairs = new LinkedList<>();
        Movie firstMovie = null;
        Movie secondMovie = null;

        int maximumNumberOfPossiblePairs = (int) (totalMovies * (totalMovies - 1) / 2); //https://stackoverflow.com/questions/18859430/how-do-i-get-the-total-number-of-unique-pairs-of-a-set-in-the-database

        do { //random, cause sequential would be boring
            Pair<Integer, Integer> idPair = MathUtils.findTwoDifferentRandomIntegers(totalMovies);

            log.debug("Trying pair of pageIndex {} of ids for new battle in game {}", idPair, game);
            if (hasPairBeenTried(idPair, triedPairs)) {
                log.debug("Pair of pageIndex {} already tried, continuing...", idPair);
                continue;
            }
            firstMovie = movieRepository.findMovieInIndex(idPair.getFirst());
            secondMovie = movieRepository.findMovieInIndex(idPair.getSecond());
            Battle persistedBattle = repository.findByGameAndLeftBracketAndRightBracket(game, firstMovie, secondMovie);
            if (persistedBattle != null) {
                log.debug("Battle between pair of pageIndex {} already occurred, continuing...", idPair);
                triedPairs.add(idPair);
                continue;
            }
            persistedBattle = repository.findByGameAndLeftBracketAndRightBracket(game, secondMovie, firstMovie);
            if (persistedBattle != null) {
                log.debug("Battle between pair of pageIndex {} already occurred, continuing...", idPair);
                triedPairs.add(idPair);
                continue;
            }
            log.debug("Battle between movies {} and {} is valid, creating battle", firstMovie, secondMovie);
            break;
        } while(maximumNumberOfPossiblePairs > triedPairs.size());
        battle.setGame(game);
        battle.setRightBracket(firstMovie);
        battle.setLeftBracket(secondMovie);
        if (firstMovie.getRating() > secondMovie.getRating()) {
            battle.setCorrectAnswer(firstMovie);
        } else {
            battle.setCorrectAnswer(secondMovie);
        }
        battle = repository.save(battle);
        return battle;
    }

    private boolean hasPairBeenTried(Pair<Integer, Integer> pair, List<Pair<Integer, Integer>> pairList) {
        return pairList.contains(Pair.of(pair.getFirst(), pair.getSecond())) || pairList.contains(Pair.of(pair.getSecond(), pair.getFirst()));
     }


}
