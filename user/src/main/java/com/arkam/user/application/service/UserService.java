package com.arkam.user.application.service;

import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.application.dto.request.UpdateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import com.arkam.user.application.mapper.UserMapper;
import com.arkam.user.application.port.in.CreateUserUseCase;
import com.arkam.user.application.port.in.GetUserUseCase;
import com.arkam.user.application.port.in.UpdateUserUseCase;
import com.arkam.user.application.port.out.KeycloakPort;
import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.User;
import com.arkam.user.infrastructure.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements CreateUserUseCase, GetUserUseCase, UpdateUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final KeycloakPort keycloakPort;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(CreateUserRequestDto requestDto) {
        String token = keycloakPort.getAdminAccessToken();
        String keycloakUserId = keycloakPort.createUser(token, requestDto);

        User user = userMapper.toUser(requestDto);
        user.setKeycloakId(keycloakUserId);

        keycloakPort.assignRealmRoleToUser(requestDto.getUsername(), "USER", keycloakUserId);
        
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(String id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UserResponseDto updateUser(String id, UpdateUserRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));

        userMapper.updateUserFromDto(user, requestDto);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }
}
