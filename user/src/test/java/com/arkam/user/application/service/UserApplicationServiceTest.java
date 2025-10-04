package com.arkam.user.application.service;

import com.arkam.user.application.dto.AddressDTO;
import com.arkam.user.application.dto.UserRequest;
import com.arkam.user.application.dto.UserResponse;
import com.arkam.user.application.port.out.KeycloakServicePort;
import com.arkam.user.application.port.out.UserRepositoryPort;
import com.arkam.user.domain.model.Address;
import com.arkam.user.domain.model.User;
import com.arkam.user.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private KeycloakServicePort keycloakService;

    @InjectMocks
    private UserApplicationService userApplicationService;

    private UserRequest validUserRequest;
    private User savedUser;
    private UserResponse expectedUserResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setCity("Test City");
        addressDTO.setState("Test State");
        addressDTO.setCountry("Test Country");
        addressDTO.setZipcode("12345");

        validUserRequest = new UserRequest();
        validUserRequest.setFirstName("John");
        validUserRequest.setLastName("Doe");
        validUserRequest.setEmail("john.doe@example.com");
        validUserRequest.setPhone("1234567890");
        validUserRequest.setUsername("johndoe");
        validUserRequest.setAddress(addressDTO);

        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Test Country");
        address.setZipcode("12345");

        savedUser = new User();
        savedUser.setId("user123");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setPhone("1234567890");
        savedUser.setKeycloakId("keycloak123");
        savedUser.setRole(UserRole.CUSTOMER);
        savedUser.setAddress(address);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());

        expectedUserResponse = new UserResponse();
        expectedUserResponse.setId("user123");
        expectedUserResponse.setKeyCloakId("keycloak123");
        expectedUserResponse.setFirstName("John");
        expectedUserResponse.setLastName("Doe");
        expectedUserResponse.setEmail("john.doe@example.com");
        expectedUserResponse.setPhone("1234567890");
        expectedUserResponse.setRole(UserRole.CUSTOMER);
        expectedUserResponse.setAddress(addressDTO);
    }

    @Test
    void createUser_Success() {
        // Given
        when(keycloakService.createUser(validUserRequest)).thenReturn("keycloak123");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // When
        Mono<UserResponse> result = userApplicationService.createUser(validUserRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedUserResponse)
                .verifyComplete();

        verify(keycloakService).createUser(validUserRequest);
        verify(keycloakService).assignRole("johndoe", "USER", "keycloak123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_InvalidRequest_ThrowsException() {
        // Given
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setFirstName("John");
        // Missing email

        // When
        Mono<UserResponse> result = userApplicationService.createUser(invalidRequest);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(keycloakService, never()).createUser(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUser_Success() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Mono.just(savedUser));

        // When
        Mono<UserResponse> result = userApplicationService.getUser("user123");

        // Then
        StepVerifier.create(result)
                .expectNext(expectedUserResponse)
                .verifyComplete();

        verify(userRepository).findById("user123");
    }

    @Test
    void getUser_NotFound_ReturnsEmpty() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<UserResponse> result = userApplicationService.getUser("nonexistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Empty Mono completes without emitting

        verify(userRepository).findById("nonexistent");
    }

    @Test
    void getAllUsers_Success() {
        // Given
        User user2 = new User();
        user2.setId("user456");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");

        when(userRepository.findAll()).thenReturn(Flux.just(savedUser, user2));

        // When
        Flux<UserResponse> result = userApplicationService.getAllUsers();

        // Then
        StepVerifier.create(result)
                .expectNextCount(2)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<UserResponse> result = userApplicationService.getAllUsers();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_Success() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe");
        updateRequest.setEmail("john.doe@example.com");

        User updatedUser = new User();
        updatedUser.setId("user123");
        updatedUser.setFirstName("John Updated");
        updatedUser.setLastName("Doe");
        updatedUser.setEmail("john.doe@example.com");
        updatedUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById("user123")).thenReturn(Mono.just(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));

        // When
        Mono<Boolean> result = userApplicationService.updateUser("user123", updateRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ReturnsFalse() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setFirstName("John Updated");

        when(userRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<Boolean> result = userApplicationService.updateUser("nonexistent", updateRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_KeycloakFailure_PropagatesError() {
        // Given
        when(keycloakService.createUser(validUserRequest))
                .thenThrow(new RuntimeException("Keycloak service unavailable"));

        // When
        Mono<UserResponse> result = userApplicationService.createUser(validUserRequest);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(keycloakService).createUser(validUserRequest);
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_RepositoryFailure_PropagatesError() {
        // Given
        when(keycloakService.createUser(validUserRequest)).thenReturn("keycloak123");
        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Database connection failed")));

        // When
        Mono<UserResponse> result = userApplicationService.createUser(validUserRequest);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(keycloakService).createUser(validUserRequest);
        verify(userRepository).save(any(User.class));
    }
}