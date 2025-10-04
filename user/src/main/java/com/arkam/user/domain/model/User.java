package com.arkam.user.domain.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role = UserRole.CUSTOMER;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Domain business rule
    public boolean isValidEmail() {
        return email != null && email.contains("@");
    }
}
