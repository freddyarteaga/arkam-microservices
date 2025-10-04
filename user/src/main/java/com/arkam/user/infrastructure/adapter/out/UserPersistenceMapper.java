package com.arkam.user.infrastructure.adapter.out;

import com.arkam.user.domain.model.Address;
import com.arkam.user.domain.model.User;
import com.arkam.user.domain.model.UserRole;
import com.arkam.user.infrastructure.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setKeycloakId(user.getKeycloakId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPhone(user.getPhone());
        entity.setRole(user.getRole().name());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        if (user.getAddress() != null) {
            UserEntity.AddressEntity addressEntity = new UserEntity.AddressEntity();
            addressEntity.setStreet(user.getAddress().getStreet());
            addressEntity.setCity(user.getAddress().getCity());
            addressEntity.setState(user.getAddress().getState());
            addressEntity.setCountry(user.getAddress().getCountry());
            addressEntity.setZipcode(user.getAddress().getZipcode());
            entity.setAddress(addressEntity);
        }
        return entity;
    }

    public User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setKeycloakId(entity.getKeycloakId());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPhone(entity.getPhone());
        user.setRole(UserRole.valueOf(entity.getRole()));
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getAddress() != null) {
            Address address = new Address();
            address.setStreet(entity.getAddress().getStreet());
            address.setCity(entity.getAddress().getCity());
            address.setState(entity.getAddress().getState());
            address.setCountry(entity.getAddress().getCountry());
            address.setZipcode(entity.getAddress().getZipcode());
            user.setAddress(address);
        }
        return user;
    }
}