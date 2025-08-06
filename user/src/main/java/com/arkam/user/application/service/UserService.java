package com.arkam.user.application.service;


import com.arkam.user.domain.model.User;
import com.arkam.user.domain.port.in.UserUseCase;


import com.arkam.user.domain.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository; // <-- port.out

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> fetchUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
                .map(existing -> {
                    // Aquí podrías tener lógica para actualizar campos
                    existing.updateFrom(updatedUser);
                    userRepository.save(existing);
                    return true;
                }).orElse(false);
    }
}
