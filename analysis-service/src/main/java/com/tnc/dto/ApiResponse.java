package com.tnc.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// The Purpose of this DTO is to provide a generic predictable structure to my API responses...
// It also includes static method that takes care of beuilding that structure. [**Enhancement**]
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean sucess;

    private String message;

    private LocalDateTime timestamp;

    private T data;

    // @static method: this creates the structure of the API response.
    // @Params: Takes the message to be displayed and the data that it needs to include in the resp.
    public static <T> ApiResponse<T> sucess(String message, T data) {

        return ApiResponse.<T>builder()
                          .sucess(true)
                          .message(message)
                          .timestamp(LocalDateTime.now())
                          .data(data)
                          .build();
    }
    
}

/*
    NOTE: 
        -- Here the code is actually very less as lombok generates all constructors [default and parameterized], and getters and setters for all private data memebers.
        -- concept on static keyword:
            1. It means the method belongs to the class. you can class it on class instance, no need to write new ApiResponse().
            2. Only 1 instance of exists. -> loaded only once in the memory tather than to individual objects or instances of the class.
            3. Object can access the static method -> but this is consider as bad practise.
        -- Concept on Java Generic:
            -- the <T> before the return type is how we declare Genric Method.
            -- but <T> already there in the class then why need to declare it again ?
                - Well static methods are kind of their own Independent methods. ==> So when you call one, it already knows what to put in the generic path.
        -- Concept of Explicit Argument:
            1. ApiResponse<T>.builder   ==> Wrong Syntax 
                -- Here we are attempting to parameterise the class name.
                -- The generic type parameters like <T> belong to the instance (objects) created with new.
                -- Becoz static methods exist at class level, we cannot pass generic arg directly on the class name when calling a static method.
            2. ApiResponse.<T>builder  ==> Correct syntax.
                -- This is called explicit Type Argument call.
                -- Here, <T> is placed right before the static method name [builder()], not on the class name. 
                -- You are explicitly telling the static builder() method: "Hey, initialize yourself using type T."
        
*/
