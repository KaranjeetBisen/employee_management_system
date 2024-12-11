package com.advancedb.advancedb.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "departments")
@Component
public class Departments {

    @Id // Marks the field as the primary key of the table.
    private Long id; // Represents the unique identifier for a department.

    @Column(name = "deptname", nullable = false) // Maps the "deptname" column in the table and ensures it cannot be null.
    private String deptname; // Represents the name of the department.


}
