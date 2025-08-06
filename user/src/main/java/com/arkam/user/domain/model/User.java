package com.arkam.user.domain.model;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;
    private String phone;
    private UserRole role = UserRole.CUSTOMER;

    private Address address;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateFrom(User other) {
        this.firstName = other.getFirstName();
        this.lastName = other.getLastName();
        this.email = other.getEmail();
        this.phone = other.getPhone();
        this.role = other.getRole();

        if (other.getAddress() != null) {
            if (this.address == null) {
                this.address = new Address();
            }
            this.address.setStreet(other.getAddress().getStreet());
            this.address.setCity(other.getAddress().getCity());
            this.address.setState(other.getAddress().getState());
            this.address.setCountry(other.getAddress().getCountry());
            this.address.setZipcode(other.getAddress().getZipcode());
        }
    }
}
