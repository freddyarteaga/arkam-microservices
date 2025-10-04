package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserRequest;
import reactor.core.publisher.Mono;

public interface UpdateUserUseCase {
    Mono<Boolean> updateUser(String id, UserRequest request);
}