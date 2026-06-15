package com.tnc.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tnc.config.AnalysisServiceConstants;
import com.tnc.dto.AnalysisStatsResponse;
import com.tnc.dto.AnalyzeResponse;
import com.tnc.dto.HistoryResponse;
import com.tnc.entity.AnalysisResult;
import com.tnc.entity.AnalysisSource;
import com.tnc.entity.AnalysisStatus;
import com.tnc.entity.RiskLevel;
import com.tnc.repository.AnalysisResultRepository;
import com.tnc.specification.AnalysisSpecification;

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
    
    // This method -> takes username [passed in the header by gate-way service] -> calls repository -> fetches records by username sorted by createdDate.
    // Updated: added Pagination support.
    public Page<HistoryResponse> getHistory(String username, String riskLevel, String keyword, String sortBy, String direction, int page, int size) {
        
        // define how we want the data to be sorted -> Keeping it dynamic
        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
                                       ? Sort.Direction.ASC
                                       : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(sortDirection, sortBy)
        );
        
        // Creating the Specification builder for dynamic handle the filtering + Searching.
        Specification<AnalysisResult> specification = Specification.where(AnalysisSpecification.hasUsername(username))
                                                                   .and(AnalysisSpecification.hasRiskLevel(riskLevel != null ? RiskLevel.valueOf(riskLevel.toUpperCase()): null))
                                                                   .and(AnalysisSpecification.hasKeyword(keyword));
        
        // fetch the current page from JPArepository
        // update: we are sending the above specification here dynamically based on the filter selected.
        Page<AnalysisResult> results = analysisResultRepository.findAll(specification, pageable);

        // Map to page of AnalysisResult [fields from databases] to HistoryResponse [fields we want to send to client]
        // using .map() built-in from Page -> preserves the paginatation meta-data [totalPages, pageNumber, pageSize] ==> Very efficient than using List
        return results.map(this::mapToHistoryResponse);
                                       
    }

    // private method -> role is to pick only those filed that needs to be shown in client.
    // we building the HistoryResponse from the data we got from result.
    private HistoryResponse mapToHistoryResponse(AnalysisResult result) {

            return new HistoryResponse(
                result.getId(), 
                result.getSafetyScore(), 
                RiskLevel.fromSafetyScore(result.getSafetyScore()), 
                result.getTitle(), 
                result.getSummary(), 
                result.getCreatedAt()
            );
    }

    // @method -> This method is responsible for fetching Analysis Stats of logged-in user.
    public AnalysisStatsResponse getAnalysisStats(String username) {

        long totalAnalysesCount = analysisResultRepository.countByUsername(username);

        // fetches the now of records per user per Analysis State count.
        long safeCount = analysisResultRepository.countByUsernameAndSafetyScoreBetween(username, RiskLevel.SAFE.getMinScore(), RiskLevel.SAFE.getMaxScore());
        long moderateCount = analysisResultRepository.countByUsernameAndSafetyScoreBetween(username, RiskLevel.MODERATE.getMinScore(), RiskLevel.MODERATE.getMaxScore());
        long highRiskCount = analysisResultRepository.countByUsernameAndSafetyScoreBetween(username, RiskLevel.HIGH_RISK.getMinScore(), RiskLevel.HIGH_RISK.getMaxScore());
        long criticalCount = analysisResultRepository.countByUsernameAndSafetyScoreBetween(username, RiskLevel.CRITICAL.getMinScore(), RiskLevel.CRITICAL.getMaxScore());

        Double avgSafetyScore = analysisResultRepository.findAverageSafetyScoreByUsername(username);
        
        // Return a new AnalysisStatusResponse, prepared from the above values.
        // NOTE: case if a user has no records -> then avgSafetyScroe will give null -> Handle it explicitly.
        return AnalysisStatsResponse.builder()
                                    .totalAnalyses(totalAnalysesCount)
                                    .safeCount(safeCount)
                                    .moderateCount(moderateCount)
                                    .highRiskCount(highRiskCount)
                                    .criticalCount(criticalCount)
                                    .averageSafetyScore(
                                        avgSafetyScore == null 
                                            ? 0.0
                                            : Math.round(avgSafetyScore * 100.0) / 100.0
                                    )
                                    .build();

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


    Enhancement:
        -- For Handling history API, 2 new methods added:
            1. getHistory:
                -- Takes "username" coming from controller layer from the header of the request -> attached by gate-way controller.
                -- here main goal:
                    - seperate the client response from DB columns -> avoid tight coupling.
                    - This gives us flexibility to add new columns in DB without worrying of explosing them to client.
                NOTE:
                    - Here we can do simple iteration like: 
                        List<HistoryResponse> response = new ArrayList<>();

                        for (AnalysisResult result : results) {
                            response.add(mapToHistoryResponse(result));
                        }

                        return response;

                    But this is a better way:
                        results.stream()
                                .map(this::mapToHistoryResponse)
                                .toList();
                Reason:
                    -- easier to read, easier to test and only need to modify 1 method in case the result from db changes.
                
            2. mapToHistoryResponse:
                -- this basically, do the mapping of the fields from result from the db -> to historyResponse DTO.
                -- NOTE: 
                    - here we are crating a new HistoryResponse from stratch with the extracted fields from AnalysisResult.
                    - The RiskLevel feild is something that is dependent on the safetyScore -> so the RiskLevel Eunum class handles which level to show based on the score.

    Enhancements:
        -- Adding Pagaination support here on the getHistory method.
            -> Defining how we want tthe data sorted and map it to HistoryResponse structure.
            -> the use of Page.map() --> follows: Page -> map() -> Page<Response>
            -> it preserves: totoalPages, totalElements, pageNumber and pageSize. 
        
        -- To support multiple filter: we have removed specific methods from JpaRepo.
            -> Instead we are using Jpa Specifications -> Like a builder, that stacks filters if they are needed.

        -- Added a new method to handle the business logic for fetching Analysis Stats meta-data per user.
            -> This integrates to AnalysisResultRepository -> which has dervied methods and JPQL queries.
        
*/
