package com.tnc.specification;

import org.springframework.data.jpa.domain.Specification;

import com.tnc.entity.AnalysisResult;
import com.tnc.entity.RiskLevel;

/*
    This class builds the Filter including filterByUsername + filterByriskLevel + sorting + keyword searching.
    This can be extended furtur to support filter and data handling for the queries from database.
    This appraoch reduces efforts to write multiple derived query methods in repository class.
*/
public class AnalysisSpecification {
    
    // @method: This specify the filter on the usernames for the rows fetched from db.
    public static Specification<AnalysisResult> hasUsername(String username) {

        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("username"), username);

    }

    // @method: This specify the filter based on the given RiskLevel.
    public static Specification<AnalysisResult> hasRiskLevel(
        RiskLevel riskLevel
    ) {
        // Check if the riskLevel sepecification is menthioned or not ?
        if(riskLevel == null){
            return null;
        }

        // since, we know the range of safety score to decide the it's riskLevel.
        // for filtering data based on riskLevel we are using different cases.
        // Enhancement: We have updated RiskEnum to store it's guidedrange.
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(
            root.get("safetyScore"), 
            riskLevel.getMinScore(),
            riskLevel.getMaxScore() 
        );
    }
    
    // @method: This specify the filter based on the given keyword. -> searches in summary.
    public static Specification<AnalysisResult> hasKeyword(String keyword) {

        // check if the filter is with keyword is sent...
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        // extract the summary in response and return the filtered  data...
        return (root, query, criteriaBuilder) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";

            // the below basically means: Where (Lower(summary) LIKE '%keyword%') or (Lower(title) LIKE '%keyword%') 
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern), 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("summary")), pattern)
            );
        };
    }
}
