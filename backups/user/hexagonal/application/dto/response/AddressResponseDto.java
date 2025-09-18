package com.arkam.user.application.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}
