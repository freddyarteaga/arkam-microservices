package com.arkam.user.domain.port.in;

import com.arkam.user.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserUseCase {
    List<User> fetchAllUsers();
    Optional<User> fetchUserById(String id);
    void createUser(User user);
    boolean updateUser(String id, User user);
}