package com.tnc.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeResponse {

    private int score;
    private String summary;
    private List<String> redFlags;

}

/*
Later we’ll evolve this into:
    highlights
    severity levels
    AI explanations
    clause categories
    confidence score
*/