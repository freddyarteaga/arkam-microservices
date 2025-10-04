package com.arkam.user.application.port.in;

import com.arkam.user.application.dto.UserResponse;
import java.util.List;

public interface GetAllUsersUseCase {
    List<UserResponse> getAllUsers();
}