package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserRequest;
import com.arkam.user.application.dto.UserResponse;

public interface CreateUserUseCase {
    UserResponse createUser(UserRequest request);
}