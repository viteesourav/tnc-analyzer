package com.tnc.service;

import org.springframework.stereotype.Service;

import com.tnc.dto.AnalyzeResponse;

@Service
public class AnalysisService {

    private final GeminiService geminiService;

    public AnalysisService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    // This method => Calls GeminiService, take the response and returns to Controller.
    public AnalyzeResponse analyzeText(String text) throws Exception {

        // text max length validation...
        if(text.length() > 50000) {
            throw new IllegalArgumentException(
                "Document exceeds supported size"
            );
        }

        return geminiService.analyzeTerms(text);
    }   
}


/* 
    Service class -> Holds the actual Implementation of the analysis-service.
        1. It will hold the integration with AI.
        2. Work on building the response -> Return to the controller to send it back to client.
    
    Enhancement:
        -- Removed the mocked API response. -> Instead it returns what the GeminiService returns.
        -- Added a simple Input validation check:
            -> checks if the input is empty or null. ==> Added at Controller Layer. [@NotBlank + @Valid]
            -> Here we can business layer Validation on Input like: 
               the input cannot be exceed maximum of 50,000 characters.

*/
