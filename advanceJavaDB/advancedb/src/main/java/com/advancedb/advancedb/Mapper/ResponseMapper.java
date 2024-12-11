package com.advancedb.advancedb.Mapper;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.advancedb.advancedb.model.Response;

@Service
public class ResponseMapper {
    // Utility method to create a response object
    public Response createResponse(Map<String, Object> data, String status, int statusCode, HttpStatus statusMsg,
            String reqId) {
        Response response = new Response();

        Map<String, Object> statusDetails = new LinkedHashMap<>();
        statusDetails.put("status", status);
        statusDetails.put("status_code", statusCode);
        statusDetails.put("status_msg", statusMsg);
        statusDetails.put("_reqid", reqId);
        statusDetails.put("_server_ts", Instant.now().toString());

        // Set status details separately
        response.setStatus_details(statusDetails);

        // Set the input values and result in the 'data' section
        response.setData(data);

        return response;
    }
}
