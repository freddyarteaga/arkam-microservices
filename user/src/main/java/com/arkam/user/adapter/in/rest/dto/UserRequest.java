package com.arkam.user.adapter.in.rest.dto;

import com.arkam.user.domain.model.UserRole;
import lombok.Data;

@Data
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AddressDTO address;
    private UserRole role;
}
