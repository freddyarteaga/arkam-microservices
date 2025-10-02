package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import reactor.core.publisher.Mono;

public interface CreateUserUseCase {
    UserResponseDto createUser(CreateUserRequestDto requestDto);
    Mono<UserResponseDto> createUserReactive(CreateUserRequestDto requestDto);
}
