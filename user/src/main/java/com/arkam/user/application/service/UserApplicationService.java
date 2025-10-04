package com.arkam.user.application.service;

import com.arkam.user.application.dto.UserRequest;
import com.arkam.user.application.dto.UserResponse;
import com.arkam.user.application.port.in.CreateUserUseCase;
import com.arkam.user.application.port.in.GetAllUsersUseCase;
import com.arkam.user.application.port.in.GetUserUseCase;
import com.arkam.user.application.port.in.UpdateUserUseCase;
import com.arkam.user.application.port.out.KeycloakServicePort;
import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.Address;
import com.arkam.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserApplicationService implements CreateUserUseCase, GetUserUseCase, GetAllUsersUseCase, UpdateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final KeycloakServicePort keycloakService;

    @Override
    public UserResponse createUser(UserRequest request) {
        // Domain validation
        if (!isValidRequest(request)) {
            throw new IllegalArgumentException("Invalid user request");
        }

        String keycloakId = keycloakService.createUser(request);
        keycloakService.assignRole(request.getUsername(), "USER", keycloakId);

        User user = mapToDomain(request);
        user.setKeycloakId(keycloakId);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public Optional<UserResponse> getUser(String id) {
        return userRepository.findById(id).map(this::mapToResponse);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateUser(String id, UserRequest request) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            updateUserFromRequest(existingUser, request);
            existingUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    private boolean isValidRequest(UserRequest request) {
        return request != null && request.getEmail() != null && request.getEmail().contains("@");
    }

    private User mapToDomain(UserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getAddress() != null) {
            Address address = new Address();
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setState(request.getAddress().getState());
            address.setCountry(request.getAddress().getCountry());
            address.setZipcode(request.getAddress().getZipcode());
            user.setAddress(address);
        }
        return user;
    }

    private void updateUserFromRequest(User user, UserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getAddress() != null) {
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setState(request.getAddress().getState());
            address.setCountry(request.getAddress().getCountry());
            address.setZipcode(request.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setKeyCloakId(user.getKeycloakId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        if (user.getAddress() != null) {
            com.arkam.user.application.dto.AddressDTO addressDTO = new com.arkam.user.application.dto.AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            response.setAddress(addressDTO);
        }
        return response;
    }
}