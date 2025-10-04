package com.arkam.order.infrastructure.adapter.out;

import com.arkam.order.application.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final WebClient webClient;

    public Mono<UserResponse> getUserDetails(String id) {
        return webClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(ex -> Mono.empty()); // Handle errors gracefully
    }

    public Flux<UserResponse> getAllUsers() {
        return webClient.get()
                .uri("/api/users")
                .retrieve()
                .bodyToFlux(UserResponse.class);
    }
}
