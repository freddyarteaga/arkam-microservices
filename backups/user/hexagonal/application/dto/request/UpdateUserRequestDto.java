package com.arkam.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    private String email;
    private String phone;
    private AddressDTO address;
}
