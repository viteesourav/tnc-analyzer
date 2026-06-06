package com.tnc.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.tnc.config.AnalysisServiceConstants;
import com.tnc.dto.AnalyzeResponse;
import com.tnc.entity.AnalysisResult;
import com.tnc.entity.AnalysisSource;
import com.tnc.entity.AnalysisStatus;
import com.tnc.repository.AnalysisResultRepository;

@Service
public class AnalysisService {

    private final GeminiService geminiService;
    private final AnalysisResultRepository analysisResultRepository;    // This handles saving analysis data for Analysis history.

    public AnalysisService(GeminiService geminiService, AnalysisResultRepository analysisResultRepository) {
        this.geminiService = geminiService;
        this.analysisResultRepository = analysisResultRepository;
    }

    // This method => Calls GeminiService, take the response and returns to Controller.
    public AnalyzeResponse analyzeText(String text, String username) throws Exception {

        // text max length validation...
        if(text.length() > AnalysisServiceConstants.MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                "Document exceeds supported size"
            );
        }

        AnalyzeResponse response = geminiService.analyzeTerms(text);

        // Below we are building data for Analysis history and will save this in database for User Data Analytics.
        AnalysisResult result = AnalysisResult.builder()
                                            .username(username)
                                            .title(response.getTitle())
                                            .safetyScore(response.getSafetyScore())
                                            .summary(response.getSummary())
                                            .source(AnalysisSource.TEXT)
                                            .model(AnalysisServiceConstants.MODEL)
                                            .status(AnalysisStatus.COMPLETED)
                                            .createdAt(LocalDateTime.now())
                                            .build();
        
        // let's save the response in the database --> we already have the springData JPA injected to help us..
        analysisResultRepository.save(result);

        return response;
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
    
    Enhancement:
        -- So after Receiving response from GeminiSErvier -> We build the AnalysisResult DTO [using Gemini Response] --> save it DB for Analysis History -> Return the GiminiResponse back.
        -- Common Constants between all layers in analysisService is moved to a seperate Constant file.

*/
