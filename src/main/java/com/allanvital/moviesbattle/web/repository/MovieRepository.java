package com.allanvital.moviesbattle.web.repository;

import com.allanvital.moviesbattle.web.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MovieRepository extends PagingAndSortingRepository<Movie, Integer> {

    long count();
    Page<Movie> findAllByOrderByIdAsc(Pageable pageable);
    default Movie findMovieInIndex(int index) {
        Page<Movie> movies = this.findAllByOrderByIdAsc(PageRequest.of(index, 1));
        Movie movie = null;
        if (movies.hasContent()) {
            movie = movies.getContent().get(0);
        }
        return movie;
    }

}
