package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserResponse;
import java.util.Optional;

public interface GetUserUseCase {
    Optional<UserResponse> getUser(String id);
}