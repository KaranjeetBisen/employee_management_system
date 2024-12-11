package com.advancedb.advancedb.model;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Lombok annotation to auto-generate getters, setters, toString, equals, and hashCode methods
@AllArgsConstructor  // Lombok annotation to generate a constructor with all fields as parameters
@NoArgsConstructor   // Lombok annotation to generate a default constructor (no arguments)
@Component  // Marks the class as a Spring component for dependency injection
public class Request {

    private String token;          // Field to store a token (could be for authorization or identification)
    
    @Valid
    private EmployeeDTO data;      // Field to store employee-related data, with validation annotations applied

    private String _reqid;         // Field for a request ID (unique identifier for the request)
    private String _client_ts;     // Field for the client timestamp (the time when the client sends the request)
    private String _client_type;   // Field for the type of client (e.g., web, mobile, etc.)
}
