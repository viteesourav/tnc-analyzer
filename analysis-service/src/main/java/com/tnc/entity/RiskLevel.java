package com.tnc.entity;

/*
    This is an enum -> which based on the safety Score returned from AI response
    Provides some Risk Status to each Analysis.
*/
public enum RiskLevel {
    SAFE(90, 100),
    MODERATE(60, 89),
    HIGH_RISK(30, 59),
    CRITICAL(0, 29);

    private final int minScore;
    private final int maxScore;

    // constructor set's the context for each RiskLevel Ranges
    RiskLevel(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    // To fetch the guradrail ranges -> use setters.
    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    // @method -> Determines the RiskLevel based on the saftyscore.
    // logic: use a for-loop, loop though all the RiskLevels -> find where the input score lies ? -> return that level.
    public static RiskLevel fromSafetyScore(int score) {

        for(RiskLevel riskLevel : values()) {

            if(score >= riskLevel.minScore
                && score <= riskLevel.maxScore) {
                    return riskLevel;
                }
        }

        throw new IllegalArgumentException(
            "Invalid safety score: " + score
        );
    }
}

/*
    Enhancements:
        -- Insteading of using the guard value range explictly throughout the code base, we can let Enum class handle the same.
        -- Define Enums with Range of values -> Set them using Paramterised constructor, extract them using setters.
        Advantages:
            -- In future if the guardrails ranges updates, it will only update here in this one file + In the prompt in GeminiService.

*/
