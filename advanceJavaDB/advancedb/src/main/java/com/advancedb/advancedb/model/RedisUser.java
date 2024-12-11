package com.advancedb.advancedb.model;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component  // Indicates that this class is a Spring Component and can be injected into other Spring-managed beans
@Data  // Lombok annotation that generates getters, setters, toString, equals, and hashCode methods automatically
public class RedisUser {
    
    private String empName;  // Field to store the employee's name
    private String value;    // Field to store a value associated with the user, potentially a Redis value
}
