package com.arkam.user.application.mapper;

import com.arkam.user.application.dto.AddressDto;
import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.application.dto.request.UpdateUserRequestDto;
import com.arkam.user.application.dto.response.UserResponseDto;
import com.arkam.user.domain.model.Address;
import com.arkam.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(CreateUserRequestDto requestDto) {
        User user = new User();
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setEmail(requestDto.getEmail());
        user.setPhone(requestDto.getPhone());
        
        if (requestDto.getAddress() != null) {
            user.setAddress(toAddress(requestDto.getAddress()));
        }
        
        return user;
    }

    public UserResponseDto toResponseDto(User user) {
        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setKeycloakId(user.getKeycloakId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        
        if (user.getAddress() != null) {
            response.setAddress(toAddressDto(user.getAddress()));
        }
        
        return response;
    }

    public void updateUserFromDto(User user, UpdateUserRequestDto requestDto) {
        if (requestDto.getFirstName() != null) {
            user.setFirstName(requestDto.getFirstName());
        }
        if (requestDto.getLastName() != null) {
            user.setLastName(requestDto.getLastName());
        }
        if (requestDto.getEmail() != null) {
            user.setEmail(requestDto.getEmail());
        }
        if (requestDto.getPhone() != null) {
            user.setPhone(requestDto.getPhone());
        }
        if (requestDto.getAddress() != null) {
            user.setAddress(toAddress(requestDto.getAddress()));
        }
    }

    private Address toAddress(AddressDto addressDto) {
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setZipcode(addressDto.getZipcode());
        return address;
    }

    private AddressDto toAddressDto(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(address.getStreet());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setCountry(address.getCountry());
        addressDto.setZipcode(address.getZipcode());
        return addressDto;
    }
}
