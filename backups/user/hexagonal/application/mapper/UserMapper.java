package com.arkam.user.application.mapper;

import com.arkam.user.application.dto.request.CreateUserRequestDto;
import com.arkam.user.domain.model.User;

public class UserMapper {

    public User toUser(CreateUserRequestDto dto) {
        User user = new User();
        user.setKeycloakId(dto.get());
        user.setDescription(dto.getDescription());
        user.setPrice(dto.getPrice());
        user.setStockQuantity(dto.getStockQuantity());
        user.setCategory(dto.getCategory());
        user.setImageUrl(dto.getImageUrl());
        user.setActive(true);
        return user;
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = new UserResponse();
        response.setKeyCloakId(user.getKeycloakId());
        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        if (user.getAddress() != null) {
            AddressDTO addressDTO = new AddressDTO();
            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setCountry(user.getAddress().getCountry());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            response.setAddress(addressDTO);
        }
        return response;
    }
    
}
