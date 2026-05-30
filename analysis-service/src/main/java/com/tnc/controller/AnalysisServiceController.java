package com.tnc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tnc.dto.AnalyzeRequest;
import com.tnc.dto.AnalyzeResponse;
import com.tnc.service.AnalysisService;

import jakarta.validation.Valid;

@RestController
public class AnalysisServiceController {

    // Let's inject the dependency here for AnalysisService....
    private final AnalysisService analysisService;

    // Constructor Injection
    public AnalysisServiceController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }


    // Test API to see if the service is up and running...
    @GetMapping("/health")
    public String health() {
        return "Analysis Service is running 🚀";
    }

    // This is actual API to work on the Analysising the Terms and conditions...
    // NOTE: @Valid -> Triggers field level validaitons mentioned in the DTO -> here we have @NotBlank on text field.
    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@Valid @RequestBody AnalyzeRequest request) throws Exception {
        
        // call the service layer...
        return analysisService.analyzeText(request.getText());
    }
    
}

/*
    Role of Contoroller File is limited to only:
        1. receive request
        2. validate inputs
        3. Call service
        4. Return Response.

    Service Layer i.e @Service component stores the business logic for a service. -> @Service tells spring that this class contains business logic.
        -> Spring automatically manages it as a Bean.
    
    Enhancements:
        -> for the Controller layer in AnalyzeResponse: We added @Valid  -> In DTO Analyzerequest -> added field as @NotBlank.
        -> @NotBlank comes from "org.springframework.boot:spring-boot-starter-validation"
        -> It checks for Null, "" or " " input field test cases  --> Throws MethodArgumentNot Valid Exception.

*/
