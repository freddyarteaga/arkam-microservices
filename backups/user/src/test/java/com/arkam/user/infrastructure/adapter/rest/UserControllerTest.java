package com.arkam.user.infrastructure.adapter.rest;

import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.application.dto.request.UpdateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import com.arkam.user.application.port.in.CreateUserUseCase;
import com.arkam.user.application.port.in.GetUserUseCase;
import com.arkam.user.application.port.in.UpdateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private GetUserUseCase getUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @InjectMocks
    private UserController userController;

    private UserResponseDto userResponseDto;
    private CreateUserRequestDto createRequestDto;
    private UpdateUserRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto();
        userResponseDto.setId("USER-001");
        userResponseDto.setFirstName("testuser");
        userResponseDto.setEmail("test@example.com");

        createRequestDto = new CreateUserRequestDto();
        createRequestDto.setUsername("testuser");
        createRequestDto.setEmail("test@example.com");
        createRequestDto.setPassword("password123");

        updateRequestDto = new UpdateUserRequestDto();
        updateRequestDto.setEmail("updated@example.com");
    }

    @Test
    void getAllUsers_ShouldReturnOkResponse() {
        // Given
        List<UserResponseDto> users = Arrays.asList(userResponseDto);
        when(getUserUseCase.getAllUsers()).thenReturn(users);

        // When
        ResponseEntity<List<UserResponseDto>> response = userController.getAllUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(userResponseDto, response.getBody().get(0));
        verify(getUserUseCase).getAllUsers();
    }

    @Test
    void getAllUsers_WithEmptyList_ShouldReturnEmptyList() {
        // Given
        when(getUserUseCase.getAllUsers()).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<UserResponseDto>> response = userController.getAllUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(getUserUseCase).getAllUsers();
    }

    @Test
    void getUser_WithValidId_ShouldReturnUser() {
        // Given
        String userId = "USER-001";
        when(getUserUseCase.getUserById(userId)).thenReturn(userResponseDto);

        // When
        ResponseEntity<UserResponseDto> response = userController.getUser(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDto, response.getBody());
        verify(getUserUseCase).getUserById(userId);
    }

    @Test
    void createUser_WithValidRequest_ShouldReturnCreatedResponse() {
        // Given
        when(createUserUseCase.createUser(createRequestDto)).thenReturn(userResponseDto);

        // When
        ResponseEntity<UserResponseDto> response = userController.createUser(createRequestDto);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDto, response.getBody());
        verify(createUserUseCase).createUser(createRequestDto);
    }

    @Test
    void updateUser_WithValidId_ShouldReturnUpdatedUser() {
        // Given
        String userId = "USER-001";
        when(updateUserUseCase.updateUser(userId, updateRequestDto)).thenReturn(userResponseDto);

        // When
        ResponseEntity<UserResponseDto> response = userController.updateUser(userId, updateRequestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponseDto, response.getBody());
        verify(updateUserUseCase).updateUser(userId, updateRequestDto);
    }
}
