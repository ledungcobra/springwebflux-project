package com.example.moviesinfoservice.repository;

import com.example.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }


    @Test
    void findAll() {

        var moviesFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesFlux)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void findById() {
        var movieMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieMono)
                .expectNextMatches(movieInfo -> movieInfo.getName().equals("Dark Knight Rises"))
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var moviesInfoMono = movieInfoRepository.save(movieInfo);
        StepVerifier.create(moviesInfoMono)
                .assertNext(m -> {
                    assertNotNull(m.getMovieInfoId());
                    assertEquals("Batman Begins", m.getName());
                    assertEquals(2005, m.getYear());
                    assertEquals(List.of("Christian Bale", "Michael Cane"), m.getCast());
                    assertEquals(LocalDate.parse("2005-06-15"), m.getReleaseDate());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo(){
        var movie = movieInfoRepository.findById("abc").block();
        movie.setName("Dark Knight Rises");
        var movieMono = movieInfoRepository.save(movie);
        StepVerifier.create(movieMono)
                .assertNext(m -> {
                    assertEquals("Dark Knight Rises", m.getName());
                    assertEquals(2012, m.getYear());
                    assertEquals(List.of("Christian Bale", "Tom Hardy"), m.getCast());
                    assertEquals(LocalDate.parse("2012-07-20"), m.getReleaseDate());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo(){
        movieInfoRepository.deleteById("abc").block();
        var movieMono = movieInfoRepository.findById("abc").log();
        StepVerifier.create(movieMono)
                .verifyComplete();
    }
}