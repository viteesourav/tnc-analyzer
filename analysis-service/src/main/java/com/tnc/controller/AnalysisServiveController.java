package com.tnc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tnc.dto.AnalyzeRequest;
import com.tnc.dto.AnalyzeResponse;
import com.tnc.service.AnalysisService;

@RestController
public class AnalysisServiveController {

    // Let's inject the dependency here for AnalysisService....
    private final AnalysisService analysisService;

    // Constructor Injection
    public AnalysisServiveController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }


    // Test API to see if the service is up and running...
    @GetMapping("/health")
    public String health() {
        return "Analysis Service is running 🚀";
    }

    // This is actual API to work on the Analysising the Terms and conditions...
    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@RequestBody AnalyzeRequest request) {
        
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

*/
