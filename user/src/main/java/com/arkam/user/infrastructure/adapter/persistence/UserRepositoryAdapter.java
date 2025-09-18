package com.arkam.user.infrastructure.adapter.persistence;

import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.User;
import com.arkam.user.infrastructure.adapter.persistence.entity.UserEntity;
import com.arkam.user.infrastructure.adapter.persistence.mapper.UserPersistenceMapper;
import com.arkam.user.infrastructure.adapter.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userPersistenceMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }
}
