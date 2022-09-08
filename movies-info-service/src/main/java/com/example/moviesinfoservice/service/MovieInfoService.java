package com.example.moviesinfoservice.service;

import com.example.moviesinfoservice.domain.MovieInfo;
import com.example.moviesinfoservice.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovieInfoService {

    private final MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> save(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> findAll() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> findById(String movieInfoId) {
        return movieInfoRepository.findById(movieInfoId);
    }

    public Mono<MovieInfo> updateMovieInfo(MovieInfo movieInfo, String id) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo1 -> {
                    movieInfo1.setName(movieInfo.getName());
                    movieInfo1.setYear(movieInfo.getYear());
                    movieInfo1.setCast(movieInfo.getCast());
                    movieInfo1.setReleaseDate(movieInfo.getReleaseDate());
                    return movieInfoRepository.save(movieInfo1);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
