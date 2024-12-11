package com.advancedb.advancedb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advancedb.advancedb.Mapper.ResponseMapper;
import com.advancedb.advancedb.model.RedisUser;
import com.advancedb.advancedb.model.Response;
import com.advancedb.advancedb.model.UpdateEmplyeeContribution;
import com.advancedb.advancedb.service.RedisService;

@RestController // Marks this class as a RESTful controller that handles HTTP requests and responses
@RequestMapping("/api/redis") // Sets the base path for all endpoints in this controller as /api/redis
public class RedisController {

    @Autowired
    private RedisService redisService; // Injects RedisService to handle Redis operations (e.g., adding, updating data)

    @Autowired
    ResponseMapper responseService; // Injects ResponseMapper to handle response creation and mapping

    // Adds a new employee to Redis using POST method with employee data in request body
    @PostMapping("/add")
    public ResponseEntity<Response> addEmployee(@RequestBody RedisUser user) {
        return redisService.addEmployee(user); // Delegates the task to RedisService to add employee
    }

    // Retrieves an employee's data from Redis by employee name using GET method and PathVariable
    @GetMapping("/get/{empName}")
    public ResponseEntity<Response> getEmployeeValue(@PathVariable String empName) {
        return redisService.getEmployeeValue(empName); // Delegates the task to RedisService to get employee data by name
    }

    // Increments an employee's value (counter) in Redis by employee name using POST method
    @PostMapping("/increment/{empName}")
    public ResponseEntity<Response> incrementEmployeeValue(@PathVariable String empName) {
        return redisService.incrementEmployeeValue(empName); // Delegates the task to RedisService to increment employee's value
    }

    // Decrements an employee's value (counter) in Redis by employee name using POST method
    @PostMapping("/decrement/{empName}")
    public ResponseEntity<Response> decrementEmployeeValue(@PathVariable String empName) {
        return redisService.decrementEmployeeValue(empName); // Delegates the task to RedisService to decrement employee's value
    }

    // Updates the employee contribution details in Redis based on department name, employee ID, and contribution count
    @PostMapping("/updateEmployeeContribution")
    public ResponseEntity<Response> updateEmployeeContribution(@RequestBody UpdateEmplyeeContribution request) {
        return redisService.updateEmployeeContribution(request.getDepartmentName(), request.getEmployeeId(), request.getCount()); 
        // Delegates the task to RedisService to update employee contribution data
    }

    // Retrieves employee contribution details from Redis based on department name and employee ID using GET method and RequestParams
    @GetMapping("/getContribution")
    public ResponseEntity<Response> getContribution(@RequestParam String departmentName, @RequestParam String employeeId) {
        try {
            return redisService.getContribution(departmentName, employeeId); // Calls RedisService to get the contribution data
        } catch (Exception e) {
            // Creates a response with error message and returns an INTERNAL_SERVER_ERROR status if an exception occurs
            Response response = responseService.createResponse(null, "error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // Returns the error response
        }
    }

    // Sets the time-to-live (TTL) for an employee's record in Redis using POST method and PathVariable for employee name and seconds
    @PostMapping("/ttl/{empName}/{seconds}")
    public ResponseEntity<Response> setTTL(@PathVariable String empName, @PathVariable long seconds) {
        return redisService.setTTL(empName, seconds); // Calls RedisService to set the TTL for the employee record
    }
}
