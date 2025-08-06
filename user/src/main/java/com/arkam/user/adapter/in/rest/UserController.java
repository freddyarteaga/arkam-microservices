package com.arkam.user.adapter.in.rest;

import com.arkam.user.domain.port.in.UserUseCase;
import com.arkam.user.domain.model.User;
import com.arkam.user.adapter.in.rest.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userUseCase.fetchAllUsers()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable String id) {
        Optional<User> userOpt = userUseCase.fetchUserById(id);
        return userOpt.map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public void createUser(@RequestBody UserRequest request) {
        User user = mapToDomain(request);
        userUseCase.createUser(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable String id, @RequestBody UserRequest request) {
        User updatedUser = mapToDomain(request);
        boolean updated = userUseCase.updateUser(id, updatedUser);
        if (!updated) {
            throw new RuntimeException("User not found");
        }
    }

    // 🔁 Mapeos DTO ↔ Dominio

    private User mapToDomain(UserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        if (request.getAddress() != null) {
            user.setAddress(new com.arkam.user.domain.model.Address(
                    request.getAddress().getStreet(),
                    request.getAddress().getCity(),
                    request.getAddress().getState(),
                    request.getAddress().getCountry(),
                    request.getAddress().getZipcode()
            ));
        }

        return user;
    }

    private UserResponse mapToResponse(User user) {
        AddressDTO addressDTO = null;
        if (user.getAddress() != null) {
            addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
        }

        UserResponse response = new UserResponse();
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setAddress(addressDTO);
        return response;
    }
}