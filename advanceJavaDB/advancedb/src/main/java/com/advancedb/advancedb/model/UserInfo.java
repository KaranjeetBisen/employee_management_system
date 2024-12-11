package com.advancedb.advancedb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;           // Unique identifier for each user in the database
    private String name;      // Name of the user
    private String email;     // Email of the user, typically used for authentication
    private String password;  // Password for user authentication
    private String roles;     // Roles assigned to the user (e.g., ADMIN, USER, etc.)
}
