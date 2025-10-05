package com.arkam.order.clients;

import com.arkam.order.application.dto.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange
public interface UserServiceClient {

    @GetExchange("/api/users/{id}")
    Mono<UserResponse> getUserDetails(@PathVariable String id);
}
