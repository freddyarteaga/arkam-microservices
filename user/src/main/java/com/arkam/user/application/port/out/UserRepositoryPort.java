package com.arkam.user.application.port.out;

import com.arkam.user.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<User> save(User user);
    Mono<User> findById(String id);
    Flux<User> findAll();
}