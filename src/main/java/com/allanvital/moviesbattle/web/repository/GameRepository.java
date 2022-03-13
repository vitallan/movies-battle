package com.allanvital.moviesbattle.web.repository;

import com.allanvital.moviesbattle.web.model.Game;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRepository extends PagingAndSortingRepository<Game, Integer> {
}
