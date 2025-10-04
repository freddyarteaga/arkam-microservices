package com.arkam.user.application.port.out;

import com.arkam.user.application.dto.UserRequest;

public interface KeycloakServicePort {
    String createUser(UserRequest request);
    void assignRole(String username, String roleName, String userId);
}