package com.advancedb.advancedb.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.advancedb.advancedb.Mapper.ResponseMapper;
import com.advancedb.advancedb.model.AuthRequest;
import com.advancedb.advancedb.model.Response;
import com.advancedb.advancedb.model.UserInfo;
import com.advancedb.advancedb.service.JwtService;
import com.advancedb.advancedb.service.UserInfoService;

// Controller for user authentication and management
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserInfoService service; // Service for handling user data

    @Autowired
    private JwtService jwtService; // Service for generating JWT tokens

    @Autowired
    private AuthenticationManager authenticationManager; // Handles authentication

    @Autowired
    private ResponseMapper responseMapper; // Maps response data

    // Public endpoint for non-secure access
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    // Endpoint to add a new user
    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo); // Adds a new user to the system
    }

    // Endpoint for user profile, accessible only by users with 'ROLE_USER'
    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

    // Endpoint for admin profile, accessible only by users with 'ROLE_ADMIN'
    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    // Endpoint for authenticating user and generating a JWT token
    @PostMapping("/generateToken")
    public ResponseEntity<Response> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                // Generate token if authentication is successful
                String token = jwtService.generateToken(authRequest.getUsername());
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                data.put("token", token);

                // Create and return success response
                Response response = responseMapper.createResponse(
                        data,
                        "success",
                        HttpStatus.OK.value(),
                        HttpStatus.OK,
                        "unique-request-id"
                );

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                throw new UsernameNotFoundException("Invalid user request!"); // Error if authentication fails
            }

        } catch (Exception e) {
            // Handle any errors during authentication
            LinkedHashMap<String, Object> errorData = new LinkedHashMap<>();
            errorData.put("error", "Internal server error");
            errorData.put("details", e.getMessage());

            // Create and return error response
            Response response = responseMapper.createResponse(
                    errorData,
                    "failure",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "unique-request-id"
            );

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
