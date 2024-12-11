package com.advancedb.advancedb.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

// The Employee class represents the entity for the "employee" table in the database.
// It contains the employee information and relationships with other entities like Departments, Ranks, and other Employees (ReportsTo).

@Data // Lombok annotation that automatically generates getters, setters, equals, hashCode, and toString methods for the class.
@Entity // Marks this class as a JPA entity, mapping it to a database table.
@Table(name = "employee") // Specifies the name of the table that this entity will map to.
@Component // Marks the class as a Spring Bean to be automatically managed by the Spring container (dependency injection).
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L; // Serial version UID for serialization purposes.

    @Id // Marks the field as the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies that the primary key will be auto-generated using the identity strategy.
    private Long id; // Unique identifier for the employee in the table.

    @Column(name = "empid", nullable = false, unique = true) // Maps the "empid" column in the table and ensures it is unique and not null.
    private String empid; // Employee ID, which should be unique for every employee.

    @Column(name = "fname", nullable = false) // Maps the "fname" column in the table, ensuring it cannot be null.
    private String fname; // First name of the employee.

    @Column(name = "fullname") // Maps the "fullname" column. It may be null.
    private String fullname; // Full name of the employee.

    @Column(name = "dob", nullable = false) // Maps the "dob" column, ensuring the employee's date of birth is not null.
    private LocalDate dob; // Date of birth of the employee.

    @Column(name = "doj", nullable = false) // Maps the "doj" column, ensuring the employee's date of joining is not null.
    private LocalDate doj; // Date of joining of the employee.

    @Column(name = "salary", nullable = false) // Maps the "salary" column, ensuring the salary cannot be null.
    private Integer salary; // Salary of the employee.

    @ManyToOne // Indicates a many-to-one relationship between Employee and the ReportsTo field (i.e., an employee reports to another employee).
    @JoinColumn(name = "reportsto", referencedColumnName = "empid") // Maps the "reportsto" column to the "empid" column in the Employee table.
    private Employee reportsTo; // The employee to whom the current employee reports.

    @ManyToOne // Indicates a many-to-one relationship between Employee and Department.
    @JoinColumn(name = "deptid", referencedColumnName = "id") // Maps the "deptid" column to the "id" column in the Departments table.
    private Departments department; // Department to which the employee belongs.

    @ManyToOne // Indicates a many-to-one relationship between Employee and Rank.
    @JoinColumn(name = "rankid", referencedColumnName = "id") // Maps the "rankid" column to the "id" column in the Ranks table.
    private Ranks rank; // Rank of the employee.

    @Column(name = "createdat", nullable = false) // Maps the "createdat" column, ensuring it cannot be null.
    private LocalDate createdAt; // Date when the employee record was created.

    @Column(name = "updatedat") // Maps the "updatedat" column. This column may be null.
    private LocalDate updatedAt; // Date when the employee record was last updated.

    @Column(name = "client_reqid", nullable = false) // Maps the "client_reqid" column, ensuring it cannot be null.
    private String clientReqId; // Client request ID associated with the employee.
}
