package com.arkam.user.domain.port.out;

import com.arkam.user.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    void save(User user);
}
