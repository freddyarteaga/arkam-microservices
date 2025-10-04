package com.arkam.user.infrastructure.adapter.in;

import com.arkam.user.application.dto.AddressDTO;
import com.arkam.user.application.dto.UserRequest;
import com.arkam.user.application.dto.UserResponse;
import com.arkam.user.application.port.in.CreateUserUseCase;
import com.arkam.user.application.port.in.GetAllUsersUseCase;
import com.arkam.user.application.port.in.GetUserUseCase;
import com.arkam.user.application.port.in.UpdateUserUseCase;
import com.arkam.user.domain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @MockBean
    private GetAllUsersUseCase getAllUsersUseCase;

    @MockBean
    private UpdateUserUseCase updateUserUseCase;

    private UserRequest validUserRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
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

        userResponse = new UserResponse();
        userResponse.setId("user123");
        userResponse.setKeyCloakId("keycloak123");
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setPhone("1234567890");
        userResponse.setRole(UserRole.CUSTOMER);
        userResponse.setAddress(addressDTO);
    }

    @Test
    void createUser_Success() {
        // Given
        when(createUserUseCase.createUser(any(UserRequest.class)))
                .thenReturn(Mono.just(userResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validUserRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .isEqualTo(userResponse);
    }

    @Test
    void createUser_InvalidRequest() {
        // Given
        UserRequest invalidRequest = new UserRequest();
        invalidRequest.setFirstName("John");
        // Missing email

        when(createUserUseCase.createUser(any(UserRequest.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid user request")));

        // When & Then
        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getUser_Success() {
        // Given
        when(getUserUseCase.getUser("user123")).thenReturn(Mono.just(userResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/users/user123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .isEqualTo(userResponse);
    }

    @Test
    void getUser_NotFound() {
        // Given
        when(getUserUseCase.getUser("nonexistent")).thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/users/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllUsers_Success() {
        // Given
        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId("user456");
        userResponse2.setFirstName("Jane");
        userResponse2.setLastName("Smith");
        userResponse2.setEmail("jane.smith@example.com");
        userResponse2.setRole(UserRole.CUSTOMER);

        when(getAllUsersUseCase.getAllUsers())
                .thenReturn(Flux.just(userResponse, userResponse2));

        // When & Then
        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponse.class)
                .hasSize(2);
    }

    @Test
    void updateUser_Success() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe");
        updateRequest.setEmail("john.doe@example.com");

        when(updateUserUseCase.updateUser("user123", updateRequest))
                .thenReturn(Mono.just(true));

        // When & Then
        webTestClient.put()
                .uri("/api/users/user123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("User updated successfully");
    }

    @Test
    void updateUser_NotFound() {
        // Given
        UserRequest updateRequest = new UserRequest();
        updateRequest.setFirstName("John Updated");

        when(updateUserUseCase.updateUser("nonexistent", updateRequest))
                .thenReturn(Mono.just(false));

        // When & Then
        webTestClient.put()
                .uri("/api/users/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNotFound();
    }
}