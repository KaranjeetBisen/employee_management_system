package com.advancedb.advancedb.logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This class is a custom Spring filter that logs request and response details for incoming HTTP requests.
 * It extends OncePerRequestFilter to ensure that the filter is executed only once per request.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    // Logger instance for logging request and response information
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);


    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Wrapping the original request and response objects to enable caching their content
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        // Capturing the start time of the request
        Instant startTime = Instant.now();
        
        try {
            // Proceeding with the next filter in the filter chain
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            // Calculating the duration of the request processing
            long duration = Instant.now().toEpochMilli() - startTime.toEpochMilli();
            
            // Extracting the request body content (if any) as a string
            String reqBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
            
            // Extracting the response body content (if any) as a string
            String resBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
            
            // Logging request and response details: API, HTTP method, response status, duration, request body, and response body
            log.info(String.format(
                    "API=%s | Method=%s | Status=%d | Duration=%d ms | Request=%s | Response=%s",
                    request.getRequestURI(),
                    request.getMethod(),
                    response.getStatus(),
                    duration,
                    reqBody.trim(),
                    resBody));
            
            // Copying the response body content to the actual response
            wrappedResponse.copyBodyToResponse();
        }
    }
}
