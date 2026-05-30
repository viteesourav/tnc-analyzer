package com.tnc.exception;


// This is a custom Exception Class Extending the RuntimeExcpetion class -> It just implements it's methods.
// Purpose -> A simple Custom runtime exception class specifically to GeminiService Layer -> Handles Errors from this layer.
public class GeminiApiException extends RuntimeException {

    public GeminiApiException(String message) {
        super(message);
    }

    public GeminiApiException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
