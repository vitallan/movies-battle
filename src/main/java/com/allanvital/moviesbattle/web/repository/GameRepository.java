package com.allanvital.moviesbattle.web.repository;

import com.allanvital.moviesbattle.web.model.Game;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface GameRepository extends PagingAndSortingRepository<Game, Integer> {

    Game findByPlayerAndClosedAtIsNull(String player);
    List<Game> findByPlayer(String player);

}
