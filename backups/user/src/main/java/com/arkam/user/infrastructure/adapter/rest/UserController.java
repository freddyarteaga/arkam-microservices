package com.arkam.user.infrastructure.adapter.rest;

import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.application.dto.request.UpdateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import com.arkam.user.application.port.in.CreateUserUseCase;
import com.arkam.user.application.port.in.GetUserUseCase;
import com.arkam.user.application.port.in.UpdateUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return new ResponseEntity<>(getUserUseCase.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String id) {
        log.info("Request received for user: {}", id);
        return ResponseEntity.ok(getUserUseCase.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto userRequest) {
        UserResponseDto createdUser = createUserUseCase.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String id,
                                                     @Valid @RequestBody UpdateUserRequestDto updateUserRequest) {
        UserResponseDto updatedUser = updateUserUseCase.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Reactive endpoints
    @GetMapping("/reactive")
    public Flux<ResponseEntity<UserResponseDto>> getAllUsersReactive() {
        return getUserUseCase.getAllUsersReactive()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/reactive/{id}")
    public Mono<ResponseEntity<UserResponseDto>> getUserReactive(@PathVariable String id) {
        log.info("Reactive request received for user: {}", id);
        return getUserUseCase.getUserByIdReactive(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/reactive")
    public Mono<ResponseEntity<UserResponseDto>> createUserReactive(@Valid @RequestBody CreateUserRequestDto userRequest) {
        return createUserUseCase.createUserReactive(userRequest)
                .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED));
    }

    @PutMapping("/reactive/{id}")
    public Mono<ResponseEntity<UserResponseDto>> updateUserReactive(@PathVariable String id,
                                                                   @Valid @RequestBody UpdateUserRequestDto updateUserRequest) {
        return updateUserUseCase.updateUserReactive(id, updateUserRequest)
                .map(ResponseEntity::ok);
    }
}
