package com.advancedb.advancedb.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.advancedb.advancedb.Mapper.ResponseMapper;
import com.advancedb.advancedb.model.RedisUser;
import com.advancedb.advancedb.model.Response;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ResponseMapper responseService;

    // Add key with default value
    public ResponseEntity<Response> addEmployee( RedisUser user) {
        try {
            if (user.getValue() == null || user.getValue().isEmpty()) {
                // If not provided, set it to 1
                user.setValue("1");
            }
    
            // Set the value in Redis
            redisTemplate.opsForValue().set("user." + user.getEmpName(), user.getValue());

            // // Retrieve the value if needed and convert it to a Map if possible
            // String value = (String) redisTemplate.opsForValue().get("user." + user.getEmpName());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("empName", user.getEmpName());
            resultMap.put("value", user.getValue());

            // Use the resultMap in the response
            return new ResponseEntity<>(
                    responseService.createResponse(resultMap, "success", HttpStatus.OK.value(), HttpStatus.OK, ""),
                    HttpStatus.OK);

        } catch (Exception e) {
            // Return an error response
            return new ResponseEntity<>(responseService.createResponse(null, "error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

    // Get key value
    public ResponseEntity<Response> getEmployeeValue(String empName) {
        try {
            String value = (String) redisTemplate.opsForValue().get("user." + empName);
            if (value != null) {
                return new ResponseEntity<>(responseService.createResponse(Map.of("value", value), "success",
                        HttpStatus.OK.value(), HttpStatus.OK, ""), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(responseService.createResponse(null, "Employee not found",
                        HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, ""), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(responseService.createResponse(null, "error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

    // Increment value
    public ResponseEntity<Response> incrementEmployeeValue(String empName) {
        try {
            redisTemplate.opsForValue().increment("user." + empName);
            String value = (String) redisTemplate.opsForValue().get("user." + empName);

            return new ResponseEntity<>(responseService.createResponse(Map.of("value", value),
                    "Value incremented successfully", HttpStatus.OK.value(), HttpStatus.OK, ""), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(responseService.createResponse(null, "error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

    // Decrement value
    public ResponseEntity<Response> decrementEmployeeValue(String empName) {
        try {
            redisTemplate.opsForValue().decrement("user." + empName);
            String value = (String) redisTemplate.opsForValue().get("user." + empName);
            return new ResponseEntity<>(responseService.createResponse(Map.of("value", value),
                    "Value decremented successfully", HttpStatus.OK.value(), HttpStatus.OK, ""), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(responseService.createResponse(null, "error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

    // Update Employee Contribution with new department and increment count
    public ResponseEntity<Response> updateEmployeeContribution(String departmentName, String employeeId,
            Integer count) {
        try {
            // Construct the key
            String key = "user." + departmentName + "." + employeeId;

            // Check if the key exists
            if (redisTemplate.hasKey(key)) {
                // Increment the key value by the count (default is 1 if count is null)
                long updatedCount = redisTemplate.opsForValue().increment(key, count != null ? count : 1);

                // Creating response for successful update
                LinkedHashMap<String, Object> response = new LinkedHashMap<>();
                response.put("key", key);
                response.put("latestCount", updatedCount);

                return new ResponseEntity<>(responseService.createResponse(response,
                        "Contribution updated successfully", HttpStatus.OK.value(), HttpStatus.OK, ""), HttpStatus.OK);
            } else {
                // If the key does not exist, create it with the default value of 0
                redisTemplate.opsForValue().set(key, "0");

                // Increment the key value by the count (default is 0 + 1 if count is null)
                long updatedCount = redisTemplate.opsForValue().increment(key, count != null ? count : 1);

                // Creating response for successful update
                LinkedHashMap<String, Object> response = new LinkedHashMap<>();
                response.put("key", key);
                response.put("latestCount", updatedCount);

                return new ResponseEntity<>(responseService.createResponse(response,
                        "Contribution added and updated successfully", HttpStatus.OK.value(), HttpStatus.OK, ""),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            // If any error occurs, return error response
            return new ResponseEntity<>(responseService.createResponse(null, "Error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Response> getContribution(String departmentName, String employeeId) {
        try {
            // Construct the key
            String key = "user." + departmentName + "." + employeeId;

            // Check if the key exists in Redis
            if (redisTemplate.hasKey(key)) {
                // Retrieve the value from Redis
                String value = (String) redisTemplate.opsForValue().get(key);
                int count = Integer.parseInt(value);

                // Create response map
                LinkedHashMap<String, Object> response = new LinkedHashMap<>();
                response.put("key", key);
                response.put("latestCount", count);

                return new ResponseEntity<>(responseService.createResponse(response,
                        "Contribution retrieved successfully", HttpStatus.OK.value(), HttpStatus.OK, ""),
                        HttpStatus.OK);
            } else {
                // If the key does not exist, return a not found response
                return new ResponseEntity<>(responseService.createResponse(null, "Employee contribution not found",
                        HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, ""), HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            // If any error occurs, return error response
            return new ResponseEntity<>(responseService.createResponse(null, "Error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

    // Set TTL for key
    public ResponseEntity<Response> setTTL(String empName, long seconds) {
        try {
            redisTemplate.expire("user." + empName, Duration.ofSeconds(seconds));
            return new ResponseEntity<>(responseService.createResponse(null, "TTL set successfully",
                    HttpStatus.OK.value(), HttpStatus.OK, ""), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(responseService.createResponse(null, "error: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, ""), HttpStatus.BAD_REQUEST);
        }
    }

}