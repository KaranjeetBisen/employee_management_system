package com.advancedb.advancedb.Mapper;

import org.springframework.stereotype.Component;

import com.advancedb.advancedb.model.Employee;
import com.advancedb.advancedb.model.ShadowEmployee;

// The ShadowEmployeeMapper class is responsible for converting an Employee entity to a ShadowEmployee entity.
@Component // Marks this class as a Spring component for dependency injection
public class ShadowEmployeeMapper {

    // Converts an Employee entity to a ShadowEmployee entity.
    public ShadowEmployee toShadowEmployee(Employee employee) {

        // Creating a new ShadowEmployee object
        ShadowEmployee sEmp = new ShadowEmployee();

        // Mapping the Employee fields to the ShadowEmployee fields
        sEmp.setEmpid(employee.getEmpid()); // Setting employee ID
        sEmp.setFname(employee.getFname()); // Setting first name
        sEmp.setFullname(employee.getFullname()); // Setting full name
        sEmp.setDob(employee.getDob()); // Setting date of birth
        sEmp.setDoj(employee.getDoj()); // Setting date of joining
        sEmp.setSalary(employee.getSalary()); // Setting salary
        sEmp.setReportsTo(employee.getReportsTo()); // Setting reportsTo (supervisor/manager)
        sEmp.setDepartment(employee.getDepartment()); // Setting department
        sEmp.setRank(employee.getRank()); // Setting rank
        sEmp.setCreatedAt(employee.getCreatedAt()); // Setting the created date
        sEmp.setUpdatedAt(employee.getUpdatedAt()); // Setting the updated date
        sEmp.setClientReqId(employee.getClientReqId()); // Setting client request ID
        
        // Returning the populated ShadowEmployee object
        return sEmp;
    }
}
