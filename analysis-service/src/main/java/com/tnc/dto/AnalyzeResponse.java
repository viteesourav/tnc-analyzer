package com.tnc.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyzeResponse {

    private String title;
    private int safetyScore;
    private String summary;
    private List<FlagItem> redFlags;
    private List<FlagItem> moderateFlags;
    private List<FlagItem> safeClauses;

    /*
    Cleanest way to handle small, immutable data structure is to add a inner static record or a nested class inside DTO.
    Represents a detailed clause analysis item with a quote and AI reasoning.
    Using Java record here automatically handles getters and setters, constructors and toString() without using Lombok annotations.  
    */
    public record FlagItem(
        String clause,
        String reason
    ) {}

}

/* 
    -- Lombok + Record Combination is Perfect here:
        outer DTO -> mutable -> Lombok takes care of constructors, getters and setters.
        inner Object -> immutable -> record will take care of it.
    -- The nested structue for AnalyzeResponse.FlagItem is also Solid.
        Reason: It keeps the structure tightly coupled i.e FlagItem can only exist inside analysis response.
    -- FlagItem is perfect here because:
        1. immutable
        2. lightweight
        3. pure data holder
        4. no business logic
        -> This is what exactly Java REcords are made for.
    -- Imporant Version Note:
        Since we are using record FlagItem(...) ==> Jackson needs 17+ support -> We are on Java 21 so we are good !
    -- @JsonIgnoreProperties:
        -> This annotation ignores any additional field that AI response might add. It will ignore it.
    
    Enhancements:
        -> added title filed, needed for storing in the Analysis_result table for Analysis history.
*/