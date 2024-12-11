package com.advancedb.advancedb.Mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.advancedb.advancedb.model.Employee;
import com.advancedb.advancedb.model.EmployeeDTO;
import com.advancedb.advancedb.repository.DepartmentRepository;
import com.advancedb.advancedb.repository.EmployeeRepository;
import com.advancedb.advancedb.repository.RankRepository;

// The EmployeeMapper class is responsible for converting between Employee entity and EmployeeDTO.
@Component // Marks this class as a Spring component for dependency injection
public class EmployeeMapper {

    // Injecting dependencies of repositories for department, rank, and employee.
    @Autowired
    DepartmentRepository departmentRepository;
    
    @Autowired
    RankRepository rankRepository;
    
    @Autowired
    EmployeeRepository employeeRepository;

    // Converts an Employee entity to an EmployeeDTO object.
    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        
        // Setting fields from Employee to EmployeeDTO
        dto.setEmpid(employee.getEmpid());
        dto.setFname(employee.getFname());
        dto.setFullname(employee.getFullname());
        dto.setDob(employee.getDob());
        dto.setDoj(employee.getDoj());
        dto.setSalary(employee.getSalary());
        
        // Handling null checks for ReportsTo, DepartmentName, and RankDesc
        dto.setReportsTo(employee.getReportsTo() != null ? employee.getReportsTo().getEmpid() : null);
        dto.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getDeptname() : null);
        dto.setRankDesc(employee.getRank() != null ? employee.getRank().getRankdesc() : null);
        
        // Setting the client request ID
        dto.setClientReqId(employee.getClientReqId());
        
        // Return the populated DTO object
        return dto;
    }

    // Converts an EmployeeDTO object to an Employee entity.
    public Employee toEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        
        // Setting fields from EmployeeDTO to Employee
        employee.setEmpid(dto.getEmpid());
        employee.setFname(dto.getFname());
        employee.setFullname(dto.getFullname());
        employee.setDob(dto.getDob());
        employee.setDoj(dto.getDoj());
        employee.setSalary(dto.getSalary());

        try {
            // Fetching and setting the department, rank, and reportsTo values
            if (dto.getDepartmentName() != null) {
                employee.setDepartment(departmentRepository.findByDeptname(dto.getDepartmentName()).get(0));
            }

            if (dto.getRankDesc() != null) {
                employee.setRank(rankRepository.findByRankdesc(dto.getRankDesc()).get(0));
            }

            if (dto.getReportsTo() != null) {
                employee.setReportsTo(employeeRepository.findByEmpid(dto.getReportsTo()).get(0));
            }
        } catch (IndexOutOfBoundsException e) {
            // Throwing an exception if the department, rank, or reportsTo cannot be found
            throw new IndexOutOfBoundsException("reportsTo, rank or department may not exist!");
        }

        // Checking if the employee already exists in the database, if yes, retain creation and update timestamps
        List<Employee> existingEmployees = employeeRepository.findByEmpid(dto.getEmpid());
        if (!existingEmployees.isEmpty()) {
            Employee e = existingEmployees.get(0);
            employee.setCreatedAt(e.getCreatedAt());
            employee.setUpdatedAt(e.getUpdatedAt());
        }

        // Setting the client request ID
        employee.setClientReqId(dto.getClientReqId());

        // Returning the populated employee entity
        return employee;
    }
}
