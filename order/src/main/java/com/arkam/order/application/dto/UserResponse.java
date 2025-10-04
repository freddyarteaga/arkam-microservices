package com.arkam.order.application.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String keyCloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private AddressDTO address;
}