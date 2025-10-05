package com.arkam.order.application.port.out;

import com.arkam.order.application.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface UserServicePort {
    Mono<UserResponse> getUserDetails(String userId);
}