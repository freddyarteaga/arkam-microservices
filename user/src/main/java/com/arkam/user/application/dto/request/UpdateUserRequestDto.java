package com.arkam.user.application.dto.request;

import com.arkam.user.application.dto.AddressDto;
import jakarta.validation.constraints.Email;
import lombok.Data;



@Data
public class UpdateUserRequestDto {
    private String firstName;
    private String lastName;
    
    @Email(message = "El formato del email no es válido")
    private String email;
    
    private String phone;
    private AddressDto address;
}
