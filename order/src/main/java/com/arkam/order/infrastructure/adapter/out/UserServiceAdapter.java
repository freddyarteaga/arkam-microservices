package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.application.dto.UserResponse;
import com.arkam.order.application.port.out.UserServicePort;
import com.arkam.order.clients.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserServiceAdapter implements UserServicePort {

    private final UserServiceClient userServiceClient;

    @Override
    public Mono<UserResponse> getUserDetails(String userId) {
        return userServiceClient.getUserDetails(userId);
    }
}