package com.tnc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tnc.entity.AnalysisResult;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // Fetch Entire history timeline for a single user sorted by newest first.
    List<AnalysisResult> findByUsernameOrderByCreatedAtDesc(String username);
    
}

/*
    The above handles the SQL queires for us automatically. Thats the advantage of extending the JpaRepository.
    Spring Data JPA generates the query and fetch the details.
    -- It also Creates the table Defined at AnalysisResult Enity.
    -- Notice: I didn't define anywhere this method "findByUsernameOrderByCreatedAtDesc" ==> JPA internally generates SQL Queries for this and will fetch the data.
    
    NOTE:
        -- Here spring Data JPA parses the method name -> no implementation needed.
        -- Spring supports a lot of derived query method:
            findByUsername()
            findByUserNameAndRiskLevel()
            findTop5ByUsernameOrderByCreatedAtDesc()
            existsByUsername()
            countByRiskLevel() ... etc
*/
