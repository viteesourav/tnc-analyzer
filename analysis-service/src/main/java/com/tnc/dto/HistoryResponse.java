package com.tnc.dto;

import java.time.LocalDateTime;

import com.tnc.entity.RiskLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryResponse {
    
    private Long id;

    private int safetyScore;

    private RiskLevel riskLevel;

    private String title;

    private String summary;

    private LocalDateTime createdAt;

}

/*
    This is DTO Class
        - it's main functionality is to seperate database returned fields from what we send to Clinets.
        - basically, once data returned from Histoty API -> includes all db fields as per AnalysisResult.java.
        - we want to include few of selected -> those are mentioned here.
    Advantages:
        1. HistoryResponse is decoupled from Database fields i.e tomorrow if we store more data in DB -> it will not accidently leak to client.

*/
