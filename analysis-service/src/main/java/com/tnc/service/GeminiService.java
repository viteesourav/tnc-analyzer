package com.tnc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {
    
    private final WebClient webClient;

    // This annotation -> injects the value from application yml. Spring automatically reads config.
    @Value("${gemini.api.key}")
    private String apiKey;

    // Constructor injection
    public GeminiService(WebClient.Builder webClientBuilder) {
        // intialize the webcilent to look for the 3rd party API url.
        this.webClient = webClientBuilder.baseUrl(
            "https://generativelanguage.googleapis.com")
            .build();
    }

    public String analyzeTerms(String text) {

        // This is the prompt that will be send to LLM for generating the output.
        // A more detailed prompt will generate a predictable outcome from the model
        String prompt = """
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
        
        String requestBody = """
                {
                  "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            }
                        ]
                    }
                  ]                
                }
                """.formatted(prompt);
        
        // Updated explicitly to the robust, free-tier supported 'gemini-2.5-flash' string        
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1beta/models/gemini-2.5-flash:generateContent")
                    .queryParam("key", apiKey)
                    .build())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();       // for now forcing synchronous behaviour. [We will upgrade this by using Kafka]
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
*/
