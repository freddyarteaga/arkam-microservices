package com.arkam.user.application.port.out;

import com.arkam.user.application.dto.request.CreateUserRequestDto;

public interface KeycloakPort {
    String getAdminAccessToken();
    String createUser(String token, CreateUserRequestDto userRequest);
    void assignRealmRoleToUser(String username, String roleName, String userId);
}
