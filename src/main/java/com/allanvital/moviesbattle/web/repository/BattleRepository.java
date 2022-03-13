package com.allanvital.moviesbattle.web.repository;

import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.model.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BattleRepository extends PagingAndSortingRepository<Battle, Integer> {

    @Query("SELECT COUNT(b) FROM Battle b WHERE b.playerAnswer != b.correctAnswer AND b.game = :game")
    Integer countWrongAnswersInGame(Game game);
    Battle findByGameAndPlayerAnswerIsNull(Game game);
    Battle findFirstByGameOrderByCreatedAtDesc(Game game);
    Battle findByGameAndLeftBracketAndRightBracket(Game game, Movie leftBracket, Movie RightBracket);

}
