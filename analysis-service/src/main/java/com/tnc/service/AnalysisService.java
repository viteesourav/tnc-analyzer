package com.tnc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tnc.dto.AnalyzeResponse;

@Service
public class AnalysisService {

    private final GeminiService geminiService;

    public AnalysisService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    // This method => Calls GeminiService, take the response and returns to Controller.
    public AnalyzeResponse analyzeText(String text) {

        String apiResponse = geminiService.analyzeTerms(text);

        // Mock API response for now...
        return new AnalyzeResponse(
            65,
            apiResponse,
            List.of(
                "AI-generated analysis completed"));
    }   
}


/* 
    Service class -> Holds the actual Implementation of the analysis-service.
        1. It will hold the integration with AI.
        2. Work on building the response -> Return to the controller to send it back to client.

*/
