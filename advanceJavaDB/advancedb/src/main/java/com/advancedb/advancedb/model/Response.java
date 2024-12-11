package com.advancedb.advancedb.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Lombok annotation to auto-generate getters, setters, toString, equals, and hashCode methods
@NoArgsConstructor  // Lombok annotation to generate a default constructor (no arguments)
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields as parameters
@Component  // Marks the class as a Spring-managed bean for dependency injection
public class Response implements Serializable {

    private static final long serialVersionUID = 1L; // Used for version control of serialized objects

    private Map<String, Object> data;          // A map to hold the actual response data (dynamic content)
    private Map<String, Object> status_details; // A map to hold status-related information (such as success/error codes, messages, etc.)
}
