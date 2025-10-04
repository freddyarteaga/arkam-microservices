package com.arkam.user.application.port.out;

import com.arkam.user.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
}