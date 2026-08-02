package com.tnc.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tnc.config.AnalysisServiceConstants;
import com.tnc.dto.AnalysisStatsResponse;
import com.tnc.dto.AnalyzeRequest;
import com.tnc.dto.AnalyzeResponse;
import com.tnc.dto.HistoryResponse;
import com.tnc.entity.RiskLevel;
import com.tnc.service.AnalysisService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/analyses")
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
    // Extract the username coming in the request header --> Pass it to the service layer.
    @PostMapping
    public AnalyzeResponse analyze(@Valid @RequestBody AnalyzeRequest request, @RequestHeader("X-Authenticated-User") String username) throws Exception {

        // call the service layer...
        return analysisService.analyzeText(request.getText(), username);
    }

    // This API endpoint manages user history.
    @GetMapping
    public Page<HistoryResponse> getHistory(
        @RequestHeader("X-Authenticated-User") String username,
        @RequestParam(required = false) String riskLevel,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String direction,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) throws Exception {

        // NOTE: for RiskLevel, when client sends an invalid value, Spring throws MethodArgumentTypeMismatchedException.
        if (riskLevel != null && !riskLevel.isEmpty()) {
            try {
                RiskLevel.valueOf(riskLevel.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                    "Invalid riskLevel. Allowed values: " + 
                    Arrays.toString(RiskLevel.values())
                );
            }
        }

        // check if the sortBy is valid...
        if(!AnalysisServiceConstants.ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                "Invalid sort field"
            );
        }

        return analysisService.getHistory(username, riskLevel, keyword, sortBy, direction, page, size);
    }

    // API endpoint to returns User Stats.
    @GetMapping("/stats")
    public AnalysisStatsResponse getAnalysisStats(
        @RequestHeader("X-Authenticated-User") String username
    ) {
        return analysisService.getAnalysisStats(username);
    }

    // API endpoint to delete Analysis based on Id.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(
        @PathVariable Long id,
        @RequestHeader("X-Authenticated-User") String username
    ) {

        analysisService.deleteAnalysis(id, username);

        return ResponseEntity.noContent().build();
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
    
    Enhancements:
        -> Added @RequestHeader --> This can fetch any particular value from the requestHeader.
            -> In our case Gateway service before forwarding the req, adds the username in requestHeader -> which is extracted here.
    
    Enhancements:
        -> new "/history" API -> this allows the current loggedin user to view his past analystics.
        -> NOTE:
            -- here we are not taking username from restParam or queryParm like:
                GET /history?username=sourav or GET /history/sourav
            -- we are using: GET /history  --> this takes username securly from JWT token [api-gateway intercepts, extract usernmae, put it in request header]
        
    Problem:
        - After making the above changes: /analyze was working but /history was failing with 404 not Found.
    Root Cause:
        - The Api-gateway only allows /analyze/** to redirect to analysis-service -> Rest all are blocked.
    Solutions:
        1. First updated the Controller to have @RequestMapping(/analyses). -> Removed the route from analyzeText method.
            - this make sure the route /analyses still by default hit this method.
            - Now history API is: GET /analyses
            - Now health API is:  GET /analyses/health
        2. updated the application.yml of api-gateway service:
            - updated the predicate path from /analysis/**   to /analyses/**.
    
    NOTE:
        - by moving with a collection route format: /analyses, we are defining the actions based on the HTTP method.
        - so eg: POST /analyses  --> means analyses input & save to db.
                 GET /analyses   --> connect to db, fetch all relevant saved details of current user. basically fetching history without explictly using "/history route"

    Enhancements:
        -> For the fetching analyses results -> adding pagiantion support.
            -> Service Layer already takes care custom sorting based on "createdAt" + mapping AnalysisResult DTO to HistoryResponse DTO.
            -> here we are defaulting the pageNo -> 0 and size -> 10 by default.
        -> Added support for:
            1. Defualt values on Sorting and Filtering.
            2. Filter using RiskLevel and text keyword.
        
        NOTE: since Keyword and RiskLevel are not mandatory -> they must be marked as required false -> else Request controller will throw error.

            Problem:
                There is currently no check on the String values on sortBy and RiskLevel as they are String and Enums respectively.
            Solution:
                We can create a white list of allowed sortBy Fields. If anything else, throw an exception.
        
        -> Added a new Route "/analyses/stats" -> This fetches user analysis Stats per user.
            -> This API can be used in User Dashboard to show infromation on his past Analyses.

        -> Added a new Route DELETE "/{id}" -> This will delete a particular row from the Analysis table.
            -> Note: We are returning empty response. Only the STATUS 204 i.e NO Content verifies that the content is succssfully deleted.


*/
