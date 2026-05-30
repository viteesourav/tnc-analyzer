package com.tnc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This is for @NotBlank support..
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeRequest {

    @NotBlank(message = "The text field cannot be blank or null")
    private String text;
    
}

/*
THis is example of smart coding ! Instead of writing bloody boiler-plates for getter-setter and default and parameterised constructor, we are using lombok.
Makes life simple -> Provides out of the box support annotation for DTO.
    1. @Getter / @Setter:      Generates the standard public getText() and setText(String text) methods. Spring needs these to read from and write to the object during JSON serialization and deserialization.
    2. @NoArgsConstructor:     Generates a default, constructor with no arguments (public AnalyzeRequest() {}). This is crucial for Spring Boot. When a request comes in, Jackson (Spring's JSON parser) uses this blank constructor to instantiate the object before injecting the values.
    3. @AllArgsConstructor:    Generates a constructor that accepts all fields as arguments:
    4. @NotBlank:              it checks if the field is incoming as Null or blank  [Spring way of handling validation]

*/