package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.response.UserResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GetUserUseCase {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(String id);
    
    // Reactive methods
    Flux<UserResponseDto> getAllUsersReactive();
    Mono<UserResponseDto> getUserByIdReactive(String id);
}
