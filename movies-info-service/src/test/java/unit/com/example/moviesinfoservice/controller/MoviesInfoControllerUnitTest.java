package com.example.moviesinfoservice.controller;

import com.example.moviesinfoservice.domain.MovieInfo;
import com.example.moviesinfoservice.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.example.moviesinfoservice.controller.MoviesInfoControllerIntgTest.MOVIE_INFOS_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@Slf4j
@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;


    @Test
    void addMovieInfo() {
        var movie = new MovieInfo("1", "movie1", 2020, List.of("desc1"), LocalDate.now());
        when(movieInfoService.save(isA(MovieInfo.class)))
                .thenReturn(Mono.just(movie));
        webTestClient.post()
                .uri(MOVIE_INFOS_URL)
                .bodyValue(movie)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(m -> {
                    var movieInfo = m.getResponseBody();
                    assertNotNull(movieInfo);
                    assertEquals(movie, movieInfo);
                });
    }

    @Test
    void addMovieInfo_validation() {
        var movie = new MovieInfo("1", "", -2020, List.of("", "desc1"), LocalDate.now());
        when(movieInfoService.save(isA(MovieInfo.class)))
                .thenReturn(Mono.just(movie));
        webTestClient.post()
                .uri(MOVIE_INFOS_URL)
                .bodyValue(movie)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().consumeWith(body -> {
                    var actualError = new String(Objects.requireNonNull(body.getResponseBody()));
                    var expectedError = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be positive";
                    assertEquals(expectedError, actualError);
                });
    }


    @Test
    void getAllMovieInfos() {
        when(movieInfoService.findAll())
                .thenReturn(Flux.just(
                        new MovieInfo("1", "movie1", 2020, List.of("desc1"), LocalDate.now()),
                        new MovieInfo("2", "movie2", 2020, List.of("desc2"), LocalDate.now()),
                        new MovieInfo("3", "movie2", 2020, List.of("desc2"), LocalDate.now())
                ));
        webTestClient.get().uri(MOVIE_INFOS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        var movie = new MovieInfo("1", "movie1", 2020, List.of("desc1"), LocalDate.now());
        when(movieInfoService.findById(movie.getMovieInfoId()))
                .thenReturn(Mono.just(movie));
        webTestClient.get().uri(MOVIE_INFOS_URL + "/" + movie.getMovieInfoId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .isEqualTo(movie);
    }

    @Test
    void updateMovieInfo() {
        var movie = new MovieInfo("1", "movie1", 2020, List.of("desc1"), LocalDate.now());
        when(movieInfoService.updateMovieInfo(movie, movie.getMovieInfoId()))
                .thenReturn(Mono.just(movie));
        webTestClient.put().uri(MOVIE_INFOS_URL + "/" + movie.getMovieInfoId())
                .bodyValue(movie)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .isEqualTo(movie);
    }

    @Test
    void deleteMovieInfo() {
        var movie = new MovieInfo("1", "movie1", 2020, List.of("desc1"), LocalDate.now());
        when(movieInfoService.deleteMovieInfo(movie.getMovieInfoId()))
                .thenReturn(Mono.empty());
        webTestClient.delete().uri(MOVIE_INFOS_URL + "/" + movie.getMovieInfoId())
                .exchange()
                .expectStatus().isNoContent();
    }
}