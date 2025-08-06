package com.arkam.user.adapter.out.persistence;

import com.arkam.user.domain.model.User;
import com.arkam.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(String.valueOf(id));
    }

    @Override
    public void save(User user) {
        jpaRepository.save(user);
    }
}