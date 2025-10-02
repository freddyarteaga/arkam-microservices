package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.request.UpdateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import reactor.core.publisher.Mono;

public interface UpdateUserUseCase {
    UserResponseDto updateUser(String id, UpdateUserRequestDto requestDto);
    Mono<UserResponseDto> updateUserReactive(String id, UpdateUserRequestDto requestDto);
}
