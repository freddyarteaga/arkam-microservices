package com.arkam.user.application.service;

import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.application.dto.request.UpdateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import com.arkam.user.application.mapper.UserMapper;
import com.arkam.user.application.port.out.KeycloakPort;
import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.User;
import com.arkam.user.infrastructure.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private KeycloakPort keycloakPort;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDto userResponseDto;
    private CreateUserRequestDto createRequestDto;
    private UpdateUserRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("USER-001");
        user.setFirstName("testuser");
        user.setEmail("test@example.com");
        user.setKeycloakId("keycloak-123");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId("USER-001");
        userResponseDto.setFirstName("testuser");
        userResponseDto.setEmail("test@example.com");

        createRequestDto = new CreateUserRequestDto();
        createRequestDto.setUsername("testuser");
        createRequestDto.setFirstName("testuser");
        createRequestDto.setLastName("testlastname");
        createRequestDto.setEmail("test@example.com");
        createRequestDto.setPassword("password123");

        updateRequestDto = new UpdateUserRequestDto();
        updateRequestDto.setEmail("updated@example.com");
    }

    @Test
    void createUser_WithValidRequest_ShouldReturnUserResponse() {
        // Given
        String token = "access-token";
        String keycloakUserId = "keycloak-123";
        
        when(keycloakPort.getAdminAccessToken()).thenReturn(token);
        when(keycloakPort.createUser(token, createRequestDto)).thenReturn(keycloakUserId);
        when(userMapper.toUser(createRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.createUser(createRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(userResponseDto.getId(), result.getId());
        assertEquals(userResponseDto.getFirstName(), result.getFirstName());
        assertEquals(userResponseDto.getEmail(), result.getEmail());

        verify(keycloakPort).getAdminAccessToken();
        verify(keycloakPort).createUser(token, createRequestDto);
        verify(userMapper).toUser(createRequestDto);
        verify(keycloakPort).assignRealmRoleToUser("testuser", "USER", keycloakUserId);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void createUser_ShouldSetKeycloakId() {
        // Given
        String token = "access-token";
        String keycloakUserId = "keycloak-123";
        
        when(keycloakPort.getAdminAccessToken()).thenReturn(token);
        when(keycloakPort.createUser(token, createRequestDto)).thenReturn(keycloakUserId);
        when(userMapper.toUser(createRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        // When
        userService.createUser(createRequestDto);

        // Then
        assertEquals(keycloakUserId, user.getKeycloakId());
        verify(userRepository).save(user);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userResponseDto, result.get(0));

        verify(userRepository).findAll();
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void getAllUsers_WithNoUsers_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        String userId = "USER-001";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void getUserById_WithNonExistentId_ShouldThrowException() {
        // Given
        String userId = "NON-EXISTENT";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> 
            userService.getUserById(userId));
        
        assertEquals("Usuario no encontrado con ID: NON-EXISTENT", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userMapper, never()).toResponseDto(any());
    }

    @Test
    void updateUser_WithValidId_ShouldReturnUpdatedUser() {
        // Given
        String userId = "USER-001";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        // When
        UserResponseDto result = userService.updateUser(userId, updateRequestDto);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userMapper).updateUserFromDto(user, updateRequestDto);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(user);
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        // Given
        String userId = "NON-EXISTENT";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> 
            userService.updateUser(userId, updateRequestDto));
        
        assertEquals("Usuario no encontrado con ID: NON-EXISTENT", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }
}
