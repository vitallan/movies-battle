package com.allanvital.moviesbattle.scraping;

import com.allanvital.moviesbattle.web.model.Movie;
import com.allanvital.moviesbattle.web.repository.MovieRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

@Component
public class MovieFinder {

    private final Logger log = LoggerFactory.getLogger(MovieFinder.class);

    @Value("${web.scrapping.imdb.first.page}")
    private String imdbPage;

    @Value("${web.scraping.imdb.page.top.parameter}")
    private String imdbTopParameter;

    @Value("${web.scraping.imdb.page.bottom.parameter}")
    private String imdbBottomParameter;

    private MovieRepository movieRepository;

    public MovieFinder(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @PostConstruct
    public void crawlMovies() {
        if (hasDatabaseEnoughMovies()) {
            return;
        }
        this.saveFromUrl(imdbPage + imdbTopParameter);
        this.saveFromUrl(imdbPage + imdbBottomParameter);
    }

    private boolean hasDatabaseEnoughMovies() {
        return movieRepository.count() > 10;
    }

    private void saveFromUrl(String url) {
        Document movieListPage;
        try {
            movieListPage = Jsoup.parse(new URL(url).openStream(), "ISO-8859-1", imdbPage);
        } catch (IOException e) {
            log.error("Was not possible to reach imdb page for movie scraping");
            return;
        }
        movieListPage.select(".lister-item-content").forEach((element) ->{
            String title = element.select("h3 > a").text();
            Double rating = Double.parseDouble(element.select("div > div.inline-block.ratings-imdb-rating > strong").text());
            if(movieRepository.findByRating(rating) == null && movieRepository.findByTitle(title) == null) {
                log.info("Inserting {} with {} rating", title, rating);
                Movie movie = new Movie();
                movie.setTitle(title);
                movie.setRating(rating);
                movieRepository.save(movie);
            } else {
                log.warn("To avoid tie rules or duplicate movies, the movie {} with rating {} was not inserted", title, rating);
            }
        });
    }

}
