package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.response.UserResponseDto;

import java.util.List;

public interface GetUserUseCase {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(String id);
}
