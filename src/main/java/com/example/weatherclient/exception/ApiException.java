package com.example.weatherclient.exception;

/**
 * Custom exception to wrap API, network, parse, or response-related failures.
 */
public class ApiException extends Exception {
    
    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
