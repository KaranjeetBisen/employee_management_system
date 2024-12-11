package com.advancedb.advancedb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.advancedb.advancedb.Mapper.ShadowEmployeeMapper;
import com.advancedb.advancedb.model.Employee;
import com.advancedb.advancedb.model.ShadowEmployee;
import com.advancedb.advancedb.repository.EmployeeRepository;
import com.advancedb.advancedb.repository.ShadowEmployeeRepository;

@Service
public class ShadowEmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ShadowEmployeeRepository shadowEmployeeRepository;

    @Autowired
    ShadowEmployeeMapper shadowEmployeeMapper;

    public ResponseEntity<Object> copyAllEmployeesToShadowEmployee() {
        List<Employee> empList = employeeRepository.findAll();

        for (Employee emp : empList) {
            ShadowEmployee sEmp = shadowEmployeeMapper.toShadowEmployee(emp);
            shadowEmployeeRepository.save(sEmp);
        }
        return new ResponseEntity<>("Data copied to Shadow Class!", HttpStatus.OK);
    }

    public ResponseEntity<Object> copyEmployeeToShadowEmployee(Employee emp) {
        ShadowEmployee sEmp = shadowEmployeeMapper.toShadowEmployee(emp);
        shadowEmployeeRepository.save(sEmp);
        return new ResponseEntity<>("Data copied to Shadow Class!", HttpStatus.OK);

    }
}
