package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserRequest;

public interface UpdateUserUseCase {
    boolean updateUser(String id, UserRequest request);
}