package com.arkam.user.infrastructure.adapter.out;

import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        entity = userRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}