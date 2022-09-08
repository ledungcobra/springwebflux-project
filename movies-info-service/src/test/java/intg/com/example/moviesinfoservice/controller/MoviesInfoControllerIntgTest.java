package com.example.moviesinfoservice.controller;

import com.example.moviesinfoservice.domain.MovieInfo;
import com.example.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {

    public static final String MOVIE_INFOS_URL = "/v1/movieinfos";
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

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
    void testCreate() {
        var movieInfo = new MovieInfo(null, "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18"));

        webTestClient.post().uri(MOVIE_INFOS_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(m -> {
                    var movieResponse = m.getResponseBody();
                    assertNotNull(movieResponse);
                    assertNotNull(movieResponse.getMovieInfoId());
                });
    }

    @Test
    void getAllMovieInfos() {
        webTestClient.get().uri(MOVIE_INFOS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        webTestClient.get().uri(MOVIE_INFOS_URL + "/abc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(m -> {
                    var movieResponse = m.getResponseBody();
                    assertNotNull(movieResponse);
                    assertEquals("abc", movieResponse.getMovieInfoId());
                });
    }

    @Test
    void updateMovieInfo() {
        webTestClient.put()
                .uri(MOVIE_INFOS_URL + "/abc")
                .bodyValue(new MovieInfo("abc", "Dark Knight Rises1",
                        2012, List.of("Christian Bale1", "Tom Hardy1"), LocalDate.parse("2013-07-20")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(m -> {
                    var movieResponse = m.getResponseBody();
                    assertNotNull(movieResponse);
                    assertEquals("abc", movieResponse.getMovieInfoId());
                    assertEquals("Dark Knight Rises1", movieResponse.getName());
                    assertEquals(2012, movieResponse.getYear());
                    assertEquals(List.of("Christian Bale1", "Tom Hardy1"), movieResponse.getCast());
                    assertEquals(LocalDate.parse("2013-07-20"), movieResponse.getReleaseDate());
                });
    }

    @Test
    void deleteMovieInfo(){
        webTestClient.delete()
                .uri(MOVIE_INFOS_URL + "/abc")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }
}