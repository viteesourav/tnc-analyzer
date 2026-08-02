package com.tnc.exception;

// This is a custom Exception Class Extending the RuntimeExcpetion class -> It just implements it's methods.
// Purpose -> A simple Custom runtime exception class specifically to Analysis Service Layer.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
