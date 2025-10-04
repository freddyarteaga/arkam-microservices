package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserResponse;
import reactor.core.publisher.Flux;

public interface GetAllUsersUseCase {
    Flux<UserResponse> getAllUsers();
}