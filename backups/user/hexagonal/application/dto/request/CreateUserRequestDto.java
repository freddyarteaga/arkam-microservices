package com.arkam.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDto {
    @NotBlank(message = "El nombre es requerido")
    private String username;
    @NotBlank(message = "El primer nombre es requerido")
    private String firstName;
    @NotBlank(message = "El apellido es requerido")
    private String lastName;
    @NotBlank(message = "La contraseña es requerido")
    private String password;

    private String email;
    private String phone;
    private AddressDTO address;
}


