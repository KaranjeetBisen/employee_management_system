package com.advancedb.advancedb.exceptionHandling;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.advancedb.advancedb.Mapper.ResponseMapper;
import com.advancedb.advancedb.model.Response;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;

// Global exception handler for centralized error handling
@ControllerAdvice
@RequiredArgsConstructor // Generates constructor injection for required fields
public class GlobalExceptionHandler {

    @Autowired
    ResponseMapper responseMapper; // Mapper to create responses

    // Handles invalid method argument exceptions (e.g., validation failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // Collects validation errors and returns response
        List<String> errorList = new LinkedList<>();
        for (FieldError error : e.getFieldErrors()) {
            errorList.add(error.getDefaultMessage()); // Collects validation error messages
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errors", errorList); // Adds validation errors to response data
        Response response = responseMapper.createResponse(
                data, "Error", 400, HttpStatus.CONFLICT, UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Handles duplicate key exceptions (e.g., unique constraint violation)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response> handleDuplicateKeyException(DuplicateKeyException e) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", "DUPLICATE_ID"); // Handles case where ID already exists
        Response response = responseMapper.createResponse(
                data, "Error", 400, HttpStatus.CONFLICT, UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Handles data integrity violations (e.g., foreign key constraint violation)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", "DATA_INTEGRITY_VIOLATION"); // Handles cases where data integrity is violated
        Response response = responseMapper.createResponse(
                data, "Error", 400, HttpStatus.BAD_REQUEST, UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handles generic exceptions like index out of bounds
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<Response> handleGenericException(IndexOutOfBoundsException e) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", "INTERNAL_SERVER_ERROR: " + e.getMessage()); // Handles unexpected index errors
        Response response = responseMapper.createResponse(
                data, "Error", 500, HttpStatus.INTERNAL_SERVER_ERROR, UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Handles 'no such element' exceptions (e.g., element not found in a collection)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Response> handleNoSuchElementException(NoSuchElementException e) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", e.getMessage()); // Provides the error message from the exception
        Response response = responseMapper.createResponse(
                data, "Error", 404, HttpStatus.NOT_FOUND, UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Handles response status exceptions (e.g., HTTP error status with custom message)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Response> handleResponseStatusException(ResponseStatusException e) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", e.getMessage()); // Returns the message from ResponseStatusException
        Response response = responseMapper.createResponse(
                data, "Error", 404, HttpStatus.NOT_FOUND, UUID.randomUUID().toString());
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }

    // Handles missing request parameter exceptions (e.g., required parameter not present)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        // Provides details about the missing parameter
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("message", "Missing request parameter: " + ex.getParameterName()); // Details about the missing parameter
        statusDetails.put("status_code", HttpStatus.BAD_REQUEST.value());
        statusDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        // Returns response with status details
        Response response = new Response(null, statusDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handles generic exceptions (e.g., unexpected errors)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex) {
        // Provides generic error details
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("message", "Error: " + ex.getMessage()); // Generic error message
        statusDetails.put("status_code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        statusDetails.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        // Returns generic error response
        Response response = new Response(null, statusDetails);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handles expired JWT token exceptions
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Response> handleExpiredJwtException(ExpiredJwtException ex) {
        // Provides details about expired JWT token
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("message", "JWT token has expired. Please log in again.");
        statusDetails.put("status_code", HttpStatus.UNAUTHORIZED.value());
        statusDetails.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());

        Response response = new Response(null, statusDetails);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Handles malformed JWT token exceptions
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Response> handleMalformedJwtException(MalformedJwtException ex) {
        // Provides details about malformed JWT token
        Map<String, Object> statusDetails = new HashMap<>();
        statusDetails.put("message", "Malformed JWT token. Please check the token format.");
        statusDetails.put("status_code", HttpStatus.BAD_REQUEST.value());
        statusDetails.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());

        Response response = new Response(null, statusDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
