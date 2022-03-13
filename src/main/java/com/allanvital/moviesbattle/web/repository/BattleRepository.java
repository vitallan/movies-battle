package com.allanvital.moviesbattle.web.repository;

import com.allanvital.moviesbattle.web.model.Battle;
import com.allanvital.moviesbattle.web.model.Game;
import com.allanvital.moviesbattle.web.model.Movie;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BattleRepository extends PagingAndSortingRepository<Battle, Integer> {

    Battle findByGameAndPlayerAnswerIsNull(Game game);
    Battle findByGameAndLeftBracketAndRightBracket(Game game, Movie leftBracket, Movie RightBracket);

}
