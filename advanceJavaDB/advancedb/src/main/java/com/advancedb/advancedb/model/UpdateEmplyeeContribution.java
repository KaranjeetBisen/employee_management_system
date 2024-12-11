package com.advancedb.advancedb.model;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class UpdateEmplyeeContribution {

    private String departmentName;  // The name of the department to which the employee belongs
    private String employeeId;      // The ID of the employee whose contribution is being tracked
    private Integer count;          // The number of contributions, default can be set in the service layer if not provided

}
