package com.advancedb.advancedb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.advancedb.advancedb.model.Departments;
import com.advancedb.advancedb.repository.DepartmentRepository;

@Service
public class DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    /**
     * Retrieves all departments from the database.
     * @return List of departments with status code.
     */
    public ResponseEntity<List<Departments>> getAllDepartments() {
        try {
            return new ResponseEntity<>(departmentRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            // Log exception (if logging is implemented).
            e.getMessage();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Adds a list of departments to the database.
     * @param department List of departments to add.
     * @return Success or failure message with status code.
     */
    public ResponseEntity<String> addDepartments(List<Departments> department) {
        try {
            departmentRepository.saveAll(department);
            return new ResponseEntity<>("Department added successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            // Log exception (if logging is implemented).
            e.getMessage();
        }
        return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
    }
}
