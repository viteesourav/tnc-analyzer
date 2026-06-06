package com.tnc.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.config.AnalysisServiceConstants;
import com.tnc.dto.AnalyzeResponse;
import com.tnc.exception.GeminiApiException;

@Service
public class GeminiService {
    
    private final WebClient webClient;

    // Jackson's ObjectMapper as a bean we are using.
    private final ObjectMapper objectMapper;

    // This annotation -> injects the value from application yml. Spring automatically reads config.
    @Value("${gemini.api.key}")
    private String apiKey;

    // Constructor injection
    public GeminiService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        // intialize the webcilent to look for the 3rd party API url.
        this.webClient = webClientBuilder.baseUrl(
            "https://generativelanguage.googleapis.com")
            .build();
        
        this.objectMapper = objectMapper;
    }

    public AnalyzeResponse analyzeTerms(String text) throws Exception {

        // A more detailed prompt will generate a predictable outcome from the model
        String prompt = buildPrompt(text);

        // Building the requestBody with Map -> then JAckson safely converts it into valid Json.
        // Enhancement: Building request payload safely with JSON mode configured => Forces Gemini to send the reponse in JSon instead of markdown block eg: ```json...```
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of(
                    "parts", List.of(
                        Map.of(
                            "text", prompt
                        )
                    )
                )
            ),
            "generationConfig", Map.of(
                "responseMimeType", "application/json"
            )
        );

        // Searialize java object structure converted into JSON string.
        String jsonRequest = objectMapper.writeValueAsString(requestBody);
        
        
        /*
            TO_DO: 
                1. Replace blocking call with async processing
                2. using kafka + Redis status tarcking.
        */        
        String response =  webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1beta/models/" + AnalysisServiceConstants.MODEL +":generateContent")
                    .queryParam("key", apiKey)
                    .build())
                .header("Content-Type", "application/json")
                .bodyValue(jsonRequest)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))    // wait for a min for response => EXternal API should never hang
                .block();       // for now forcing synchronous behaviour. [We will upgrade this by using Kafka]
        
        String aiText = extractTextFromGeminiResponse(response);

        return objectMapper.readValue(aiText, AnalyzeResponse.class);
    
    }

    // method: extract the needed information from the AI response strutcute...
    private String extractTextFromGeminiResponse(String response) throws JsonProcessingException {
        
        JsonNode root = objectMapper.readTree(response);

        // Handle a check for the candidates presence in the Gemini response...
        JsonNode candidates = root.path("candidates");

        if(!candidates.isArray() || candidates.isEmpty()) {
            // throwing our own targeted custom exception...
            throw new GeminiApiException(
                "Gemini returned no candidates"
            );
        }

        return candidates.get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
    }

    // method: Takes care of building the Prompt for AI Model
    private String buildPrompt(String text) {
        return """
            You are an expert AI Terms and Conditions Risk Analyzer. Your task is to perform a rigorous legal and privacy risk assessment on the provided text.

            Analyze the text against standard consumer protection, privacy, and data safety benchmarks.

            ### Definitions & Classification Criteria:
            1. "safetyScore": An integer from 0 to 100. 
            - 90-100: Exceptionally user-friendly, minimal data collection, no predatory clauses.
            - 60-80: Standard commercial terms, typical data tracking, standard waivers.
            - 30-50: Concerning clauses, heavy data sharing, mandatory arbitration without opt-out.
            - 0-20: Highly predatory, aggressive data monetization, total waiving of user rights.
            2. "redFlags": Severe risks. Examples: Automatic monetization/selling of user biometrics/content without explicit consent, unilateral changes to terms without notice, waiving the right to sue for gross negligence, hidden fees.
            3. "moderateFlags": Notable risks requiring awareness. Examples: Automatic subscription renewals, targeted advertising tracking, broad but standard licensing of user-generated content, mandatory arbitration clauses.
            4. "safeClauses": High-value user protections. Examples: Explicit opt-outs for data sharing, clear deletion/"right to be forgotten" procedures, limited liability caps for the user.

            ### Output Constraints:
            - Return ONLY a raw, valid JSON object matching the schema below.
            - Do NOT wrap the JSON in markdown code blocks (e.g., do not use ```json).
            - Do NOT include any introductory or concluding text.
            - Every array property must exist. If no clauses match a specific flag tier, return an empty array `[]`.
            - "safetyScore" MUST be an integer number only.

            ### Output Schema:
            {
                "title": "A short, descriptive name identifying the document or company (e.g., 'Google Terms of Service Analysis').",
                "safetyScore": 0,
                "summary": "A concise, 2-3 sentence overview highlighting the overall tone and biggest takeaway of the document.",
                "redFlags": [
                    { "clause": "Exact text quote or specific reference", "reason": "Why this poses a severe risk to the user." }
                ],
                "moderateFlags": [
                    { "clause": "Exact text quote or specific reference", "reason": "Why the user should be cautious about this term." }
                ],
                "safeClauses": [
                    { "clause": "Exact text quote or specific reference", "reason": "How this actively protects the user." }
                ]
            }

            Terms and Conditions to Analyze:
            """ + text; 
    }

}

/*
    Webclient: Modern Spring HTTP Client.
        -- used for: 
            1. external APIs
            2. microservice calls
            3. async communtication.
    
    -- .block() => Webclient can be reactive/non-blocking by nature.
        -> For simplicity and testing purpose, we are keeping it disable for now.
    
    Enhancements:
        1. objectMapper:
            -- add as a spring bean. Spring already provides Jackson's Objectmapper as a Bean.
            -- Helpful in case of:
                1. Serializing request body.
                2. parsing Gemini response.
                3. Converting AI Json response to DTO.
            -- used Constructor Injection to inject the dependency, as this ensures immutability, easier testing and recommended pattern.

        2. Exception handling:
            -- Not hanlded any particular exception for now, but have added to throw a general Exception from the AnalyzeTerm method block.
        
        3. Handling requestBody for AI:
            -- Plane JSON can cause issue while formatting.
            -- We used Map + Jackson's object Mapper, This ensures:
                1. safe escaping.
                2. cleaner code
                3. easier modifications later
                4. fewer malformed JSON bugs.
        
        4. AI Response Wait Limit:
            -- AI reponse should not hang for unchecked time.
            -- added additional property "timeout" in webClient builder that wait for 1 min for the API to return a response and then fail gracefully.

        5. AI Response Handling:
            -- Instead of returning a raw String from AI API response.
            -- We take the string response from AI API ==> Extract the needed text ==> then mapp it DTO.

    Enhancements:
        1. Added an additional filed "title" in the Gemini Response structure and in prompt to fetch a short title for Analysis history.
*/
