package com.advancedb.advancedb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advancedb.advancedb.Mapper.ResponseMapper;
import com.advancedb.advancedb.model.Request;
import com.advancedb.advancedb.model.Response;
import com.advancedb.advancedb.service.EmployeeService;
import com.advancedb.advancedb.service.ShadowEmployeeService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController  // Marks this class as a RESTful controller for handling HTTP requests
@RequestMapping("/employee")  // Maps requests to /employee path for all endpoints in this controller
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;  // Injects EmployeeService to handle employee data operations

    @Autowired
    ShadowEmployeeService shadowEmployeeService;  // Injects ShadowEmployeeService for shadow employee operations

    @Autowired
    ResponseMapper responseService;  // Injects ResponseMapper for consistent response creation

    @GetMapping("copyToShadow")  // Endpoint to copy all employees to shadow
    public ResponseEntity<Object> copyAllEmployeesToShadowEmployee() {
        return shadowEmployeeService.copyAllEmployeesToShadowEmployee();  // Delegates the request to the shadow service to copy employees
    }

    @GetMapping("get")  // Endpoint to get all employees
    public ResponseEntity<Response> getAllEmployees() {
        try {
            Response response = employeeService.getAllDetailedEmployees();  // Fetches detailed employee data
            return new ResponseEntity<>(response, HttpStatus.OK);  // Returns employee data with HTTP status OK
        } catch (Exception e) {
            // Creates and returns an error response if an exception occurs
            Response response = responseService.createResponse(null, "error: Employee Data not fetched!", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }

    @GetMapping("get/{empId}")  // Endpoint to get a single employee by ID
    public ResponseEntity<Response> getEmployeeById(@PathVariable String empId) {
        try {
            Response response = employeeService.getEmployeeById(empId);  // Fetches employee data by empId
            return new ResponseEntity<>(response, HttpStatus.OK);  // Returns the employee data with HTTP status OK
        } catch (Exception e) {
            // Creates and returns an error response if an exception occurs
            Response response = responseService.createResponse(null, "error: Employee Data not fetched!", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }

    @GetMapping("flist")  // Endpoint to get filtered employees based on the filter parameter
    public ResponseEntity<Response> getFilteredEmployees(
            @RequestParam(value = "filter", required = false) String filter) {
        try {
            Response response = employeeService.getFilteredEmployees(filter);  // Fetches filtered employee data
            return new ResponseEntity<>(response, HttpStatus.OK);  // Returns filtered data with HTTP status OK
        } catch (Exception e) {
            // Creates and returns an error response if an exception occurs
            Response response = responseService.createResponse(null, "error: Unable to fetch data!", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }

    @GetMapping("getList")  // Endpoint to get all employees in a specified format (json, xml, pdf)
    public ResponseEntity<Object> getAllEmployeesInFormat(@RequestParam String type, HttpServletResponse rp) {
        try {
            Response response = (Response) employeeService.getAllEmployeesInFormat(type, rp);  // Fetches data in the requested format

            // Determine the media type based on the input type
            MediaType mediaType;
            if ("json".equalsIgnoreCase(type)) {
                mediaType = MediaType.APPLICATION_JSON;  // Set JSON media type
            } else if ("xml".equalsIgnoreCase(type)) {
                mediaType = MediaType.APPLICATION_XML;  // Set XML media type
            } else if ("pdf".equalsIgnoreCase(type)) {
                return null;  // No handling for PDF format in this method
            } else {
                return new ResponseEntity<>(response, HttpStatus.valueOf(rp.getStatus()));  // Default response for unsupported formats
            }

            return ResponseEntity.status(HttpStatus.valueOf(rp.getStatus()))
                    .contentType(mediaType)  // Returns response with the correct media type
                    .body(response);
        } catch (Exception e) {
            // Handles any errors and creates an error response
            Response response = responseService.createResponse(null, "error: Unknown error occurred!", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }

    @GetMapping("getListF")  // Endpoint to get filtered employees in a specified format (json, xml, pdf, xlsx)
    public ResponseEntity<Object> getFilteredEmployeesInFormat(@RequestParam(required = false) String filter, 
                                                            @RequestParam String type, 
                                                            HttpServletResponse rp) {
        try {
            // Fetch the response from the service layer with the specified filter and type
            Response response = (Response) employeeService.getFilteredEmployeesInFormatAndFilter(filter, type, rp);

            // Determine the media type based on the requested format
            MediaType mediaType;
            if ("json".equalsIgnoreCase(type)) {
                mediaType = MediaType.APPLICATION_JSON;
            } else if ("xml".equalsIgnoreCase(type)) {
                mediaType = MediaType.APPLICATION_XML;
            } else if ("pdf".equalsIgnoreCase(type)) {
                // For PDF, assume the response stream is handled directly in the service and no body is returned
                return ResponseEntity.ok().build();
            } else if ("xlsx".equalsIgnoreCase(type)) {
                // For Excel, assume response stream is handled in the service
                return ResponseEntity.ok().build();
            } else {
                // Handle unsupported format type
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Return the response with the appropriate media type
            return ResponseEntity.status(HttpStatus.valueOf(rp.getStatus()))
                    .contentType(mediaType)
                    .body(response);
        } catch (Exception e) {
            // Handle any errors and create an error response
            Response errorResponse = responseService.createResponse(null, "error: Unknown error occurred!",
                HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("add")  // Endpoint to add a new employee
    public ResponseEntity<Response> addEmployee(@Valid @RequestBody Request request) {
        try {
            return employeeService.addOneEmployee(request);  // Adds employee data
        } catch (Exception e) {
            // Creates and returns an error response if an exception occurs
            Response response = responseService.createResponse(null, "error: Unknown error occurred!", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }

    @PutMapping("{empid}")  // Endpoint to update an existing employee by empid
    public ResponseEntity<Response> updateEmployee(@PathVariable String empid, @Valid @RequestBody Request request) {
        try {
            Response response = employeeService.updateEmployee(empid, request);  // Updates employee data
            return new ResponseEntity<>(response, HttpStatus.OK);  // Returns updated data with HTTP status OK
        } catch (Exception e) {
            // Creates and returns an error response if an exception occurs
            Response response = responseService.createResponse(null, "error: Unknown error occurred!" + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }

    @DeleteMapping("{empid}")  // Endpoint to delete an employee by empid
    public ResponseEntity<Response> deleteEmployee(@PathVariable String empid) {
        try {
            Response response = employeeService.deleteEmployee(empid);  // Deletes employee data
            return new ResponseEntity<>(response, HttpStatus.OK);  // Returns response with HTTP status OK
        } catch (Exception e) {
            // Creates and returns an error response if an exception occurs
            Response response = responseService.createResponse(null, "error: Unknown error occurred!", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);  // Sends error response with HTTP status INTERNAL_SERVER_ERROR
        }
    }
}
