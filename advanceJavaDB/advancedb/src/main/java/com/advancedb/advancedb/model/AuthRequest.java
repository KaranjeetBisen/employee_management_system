package com.advancedb.advancedb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String username; // Represents the username used for authentication
    private String password; // Represents the password associated with the username

}