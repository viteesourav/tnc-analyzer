package com.tnc.analysis_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisServiveController {

    // Test API to see if the service is up and running...
    @GetMapping("/health")
    public String health() {
        return "Analysis Service is running 🚀";
    }

    // Demo REST endpoint to test if services can talk....
    @GetMapping("/analyze")
    public String analyze() {
        return "TnC analysis result 🚀";
    }
    
}
