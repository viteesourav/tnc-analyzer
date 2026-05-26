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
        String prompt = """
                Analyze the following Terms and Conditions.
                Identify risky clauses, summarize concerns,
                and provide a safety assessment.

                Terms:
                """;
        
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
