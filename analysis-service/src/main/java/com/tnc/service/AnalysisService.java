package com.tnc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tnc.dto.AnalyzeResponse;

@Service
public class AnalysisService {

    public AnalyzeResponse analyzeText(String text) {

        // Mock API response for now...
        return new AnalyzeResponse(
            65,
            "Moderate risk detected in Terms & Conditions",
            List.of(
                "Data sharing clause detected",
                "Auto renewal clause detected"));
    }   
}


/* 
    Service class -> Holds the actual Implementation of the analysis-service.
        1. It will hold the integration with AI.
        2. Work on building the response -> Return to the controller to send it back to client.

*/
