package com.advancedb.advancedb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.advancedb.advancedb.model.Departments;
import com.advancedb.advancedb.service.DepartmentService;

@RestController // Indicates that this class is a RESTful web service controller
@RequestMapping("/") // Sets the base URI for all endpoints in this controller
public class DepartmentController {
    
    @Autowired // Automatically injects the DepartmentService dependency
    DepartmentService departmentService;

    @GetMapping("department") // Maps HTTP GET requests to the method
    public ResponseEntity<List<Departments>> getAllDepartments() {
        // Calls the service method to get all departments and returns the response
        return departmentService.getAllDepartments();
    }

    @PostMapping("department/add") // Maps HTTP POST requests to the method
    public ResponseEntity<String> addDepartments(@RequestBody List<Departments> department) {
        // Calls the service method to add new departments and returns the response
        return departmentService.addDepartments(department);
    }
}