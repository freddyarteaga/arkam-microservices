package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface GetUserUseCase {
    Mono<UserResponse> getUser(String id);
}