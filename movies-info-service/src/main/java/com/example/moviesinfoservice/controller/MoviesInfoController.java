package com.example.moviesinfoservice.controller;

import com.example.moviesinfoservice.domain.MovieInfo;
import com.example.moviesinfoservice.repository.MovieInfoRepository;
import com.example.moviesinfoservice.service.MovieInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MoviesInfoController {

    private final MovieInfoService movieInfoService;

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return movieInfoService.save(movieInfo).log();
    }

    @GetMapping("/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/movieinfos/{movieInfoId}")
    public Mono<MovieInfo> getMovieInfoById(@PathVariable String movieInfoId) {
        return movieInfoService.findById(movieInfoId);
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<MovieInfo> updateMovieInfo(@RequestBody MovieInfo movieInfo, @PathVariable String id) {
        return movieInfoService.updateMovieInfo(movieInfo, id);
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return movieInfoService.deleteMovieInfo(id);
    }
}
