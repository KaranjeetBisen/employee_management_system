package com.advancedb.advancedb.model;

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

@Data  // Lombok annotation for generating getters, setters, equals, hashCode, and toString methods
@Entity  // Marks this class as a JPA entity to be persisted in the database
@Table(name = "shadow_employee")  // Maps the entity to the "shadow_employee" table in the database
@Component  // Marks the class as a Spring-managed bean
public class ShadowEmployee {

    @Id  // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Specifies that the primary key value will be generated automatically by the database
    private Long id;

    @Column(name = "empid", nullable = false)  // Maps this field to the "empid" column in the database; it's required
    private String empid;

    @Column(name = "fname", nullable = false)  // Maps this field to the "fname" column in the database; it's required
    private String fname;

    @Column(name = "fullname")  // Maps this field to the "fullname" column in the database
    private String fullname;

    @Column(name = "dob", nullable = false)  // Maps this field to the "dob" column in the database; it's required
    private LocalDate dob;

    @Column(name = "doj", nullable = false)  // Maps this field to the "doj" column in the database; it's required
    private LocalDate doj;

    @Column(name = "salary", nullable = false)  // Maps this field to the "salary" column in the database; it's required
    private Integer salary;

    @ManyToOne  // Marks this field as a many-to-one relationship to the Employee entity
    @JoinColumn(name = "reportsto", referencedColumnName = "empid")  // Specifies the foreign key column
    private Employee reportsTo;  // The employee this employee reports to

    @ManyToOne  // Marks this field as a many-to-one relationship to the Departments entity
    @JoinColumn(name = "deptid", referencedColumnName = "id")  // Specifies the foreign key column
    private Departments department;  // The department the employee belongs to

    @ManyToOne  // Marks this field as a many-to-one relationship to the Ranks entity
    @JoinColumn(name = "rankid", referencedColumnName = "id")  // Specifies the foreign key column
    private Ranks rank;  // The rank of the employee

    @Column(name = "createdat", nullable = false)  // Maps this field to the "createdat" column in the database; it's required
    private LocalDate createdAt;

    @Column(name = "updatedat")  // Maps this field to the "updatedat" column in the database (optional)
    private LocalDate updatedAt;

    @Column(name = "client_reqid", nullable = false)  // Maps this field to the "client_reqid" column in the database; it's required
    private String clientReqId;
}
