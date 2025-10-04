package com.arkam.user.infrastructure.adapter.out;

import com.arkam.user.domain.model.Address;
import com.arkam.user.domain.model.User;
import com.arkam.user.domain.model.UserRole;
import com.arkam.user.infrastructure.UserEntity;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPersistenceMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private User domainUser;
    private UserEntity entityUser;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Test City");
        address.setState("Test State");
        address.setCountry("Test Country");
        address.setZipcode("12345");

        domainUser = new User();
        domainUser.setId("user123");
        domainUser.setFirstName("John");
        domainUser.setLastName("Doe");
        domainUser.setEmail("john.doe@example.com");
        domainUser.setPhone("1234567890");
        domainUser.setKeycloakId("keycloak123");
        domainUser.setRole(UserRole.CUSTOMER);
        domainUser.setAddress(address);
        domainUser.setCreatedAt(LocalDateTime.now());
        domainUser.setUpdatedAt(LocalDateTime.now());

        entityUser = new UserEntity();
        entityUser.setId("user123");
        entityUser.setFirstName("John");
        entityUser.setLastName("Doe");
        entityUser.setEmail("john.doe@example.com");
        entityUser.setPhone("1234567890");
        entityUser.setKeycloakId("keycloak123");
        entityUser.setRole("CUSTOMER");
        entityUser.setCreatedAt(LocalDateTime.now());
        entityUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void save_Success() {
        // Given
        when(mapper.toEntity(domainUser)).thenReturn(entityUser);
        when(userRepository.save(entityUser)).thenReturn(Mono.just(entityUser));
        when(mapper.toDomain(entityUser)).thenReturn(domainUser);

        // When
        Mono<User> result = userRepositoryAdapter.save(domainUser);

        // Then
        StepVerifier.create(result)
                .expectNext(domainUser)
                .verifyComplete();
    }

    @Test
    void findById_Success() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Mono.just(entityUser));
        when(mapper.toDomain(entityUser)).thenReturn(domainUser);

        // When
        Mono<User> result = userRepositoryAdapter.findById("user123");

        // Then
        StepVerifier.create(result)
                .expectNext(domainUser)
                .verifyComplete();
    }

    @Test
    void findById_NotFound() {
        // Given
        when(userRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // When
        Mono<User> result = userRepositoryAdapter.findById("nonexistent");

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Empty Mono completes without emitting
    }

    @Test
    void findAll_Success() {
        // Given
        UserEntity entityUser2 = new UserEntity();
        entityUser2.setId("user456");
        entityUser2.setFirstName("Jane");
        entityUser2.setLastName("Smith");

        User domainUser2 = new User();
        domainUser2.setId("user456");
        domainUser2.setFirstName("Jane");
        domainUser2.setLastName("Smith");

        when(userRepository.findAll()).thenReturn(Flux.just(entityUser, entityUser2));
        when(mapper.toDomain(entityUser)).thenReturn(domainUser);
        when(mapper.toDomain(entityUser2)).thenReturn(domainUser2);

        // When
        Flux<User> result = userRepositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .expectNext(domainUser)
                .expectNext(domainUser2)
                .verifyComplete();
    }

    @Test
    void findAll_Empty() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<User> result = userRepositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
}