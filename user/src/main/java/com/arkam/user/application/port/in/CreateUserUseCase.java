package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserRequest;
import com.arkam.user.application.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface CreateUserUseCase {
    Mono<UserResponse> createUser(UserRequest request);
}