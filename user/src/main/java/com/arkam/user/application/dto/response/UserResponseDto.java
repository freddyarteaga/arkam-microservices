package com.arkam.user.application.dto.response;

import com.arkam.user.application.dto.AddressDto;
import com.arkam.user.domain.model.UserRole;
import lombok.Data;

@Data
public class UserResponseDto {
    private String id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private AddressDto address;
}
