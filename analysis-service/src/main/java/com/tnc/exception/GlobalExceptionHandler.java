package com.tnc.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j   // 🚀 Injects a standard "log" field into the class automatically using Lombok
@RestControllerAdvice
public class GlobalExceptionHandler {

    // The below is automatically inject by the above annotation @Slf4j
    // private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Catches Spring's @Valid Controller errors -> In Controller layer.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    // Catches your manual Service Layer IllegalArgumentExceptions  -> In Service Layer.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                                 .body(ex.getMessage());
    }

    // Catches Targeted Downstream Third-Party AI Exception Handler -> GeminiService Layer, Processing the AI API response
    @ExceptionHandler(GeminiApiException.class)
    public ResponseEntity<Map<String, Object>> handleGeminiApiException(GeminiApiException ex) {

        // 🔴 Logs a concise error message on the console for known execution failures
        log.error("Execution failure encountered during processing: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Execution Failure");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_GATEWAY.value());

        // Returning HTTP 502 Bad Gateway because an upstream provider failed expectations
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    // Catches your manual ResourceNotFoundException from service Layer -> Formats the error structure.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {

        // 🔴 Logs a concise error message on the console for known execution failures
        log.error("RuntimeTime Exceptions failure encountered during processing: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Resource Not Found");
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.NOT_FOUND.value());

        // Returning HTTP 502 Bad Gateway because an upstream provider failed expectations
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // The Ultimate Fallback Global Safety Net for any unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllUnCaughtExceptions(Exception ex) {

        // 🚨 CRITICAL: Passing 'ex' as the second argument forces SLF4J to print the entire raw Stack Trace!
        log.error("CRITICAL: An unexpected system error occurred in the application pipeline!", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected system error occurred. Please try again later.");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Log the actual exception details (ex) on your server console so you can debug it later,
        // while shielding the client from internal system details!
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    } 
    
}

/*
    A GlobalException Class -> To Handle all Exception structure from the server tier.
    
    Problem: 
        So Under GeminiService, ServiceLayer, Controller Layer -> we are throwing exceptions from methods -> inside we throw particular exceptions like
            RunTime exceptions, IllegalArgumentExceptions, or In one case we are throwing MethodArgument error from @Valid + @NotBlank from Spring validation.
        The Problem is this excpetions will print ugly stack-trace and lot of informations in the client which is not relevant also might throw some sensitive server infromation to client.

    Solution:
        Create a globalExceptionHandler 
        Purpose:
            1. As the exceptions bubbles up -> @RestControllerAdvice [@ControllerAdvice + @ResponseBody]
            2. @ControllerAdvice tells Spring that this class will intercept requests and exceptions across all controllers. ==> Acts as "interceptor"
            3. @ResponseBody -> returns the response in a JSON formate.
        
        Thus, It's job is take all the Exception Jargons that Spring catches -> Create a clean JSON that holds only relevany message/infroamtion + Correct Status Code -> Return to client.

    NOTE:
        -> Here we are using HashMap to gather keys that we send for the exceptions. => An Enchancement would be create a different DTO for error and use it. 


*/