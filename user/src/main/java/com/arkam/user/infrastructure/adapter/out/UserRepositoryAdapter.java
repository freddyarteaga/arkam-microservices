package com.arkam.user.infrastructure.adapter.out;

import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Mono<User> save(User user) {
        var entity = mapper.toEntity(user);
        return userRepository.save(entity)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<User> findById(String id) {
        return userRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll()
                .map(mapper::toDomain);
    }
}