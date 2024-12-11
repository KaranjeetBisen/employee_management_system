package com.advancedb.advancedb.Mapper;

import org.springframework.stereotype.Service;

import com.advancedb.advancedb.model.EmployeeDTO;
import com.advancedb.advancedb.model.Request;

@Service
public class RequestMapper {

    public EmployeeDTO requestEmployeeDTO(Request request) {
        EmployeeDTO data = request.getData();
        return data;

    }

}
