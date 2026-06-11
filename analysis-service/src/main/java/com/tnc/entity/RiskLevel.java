package com.tnc.entity;

/*
    This is an enum -> which based on the safety Score returned from AI response
    Provides some Risk Status to each Analysis.
*/
public enum RiskLevel {
    SAFE,
    MODERATE,
    HIGH_RISK,
    CRITICAL;

    public static RiskLevel fromSafetyScore(int score) {

        if (score >= 90) {
            return SAFE;
        }

        if (score >= 60) {
            return MODERATE;
        }
        
        if (score >= 30) {
            return HIGH_RISK;
        }

        return CRITICAL;
    }
}
