package com.tnc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// The Purpose of this DTO: map fields that we are expecting for Analysis Status Report API.
// NOTE: lombok takes care of generating the boiler plate for Getter-Setters-DefaultConstructors-ParameterisedConstructors
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisStatsResponse {

    private long totalAnalyses;

    private long safeCount;
    
    private long moderateCount;
    
    private long highRiskCount;
    
    private long criticalCount;
    
    private double averageSafetyScore;

}
