package com.tnc.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tnc.entity.AnalysisResult;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    // Fetch Entire history timeline for a single user sorted by newest first.
    Page<AnalysisResult> findByUsername(String username, Pageable pageable);
    
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

    Enahancement:
        -- Adding pagination support using Pageable from org.springframework.data.domain.
        -- Spring data JPA, understands pageable and automatically adds LIMTI and OFFEST clauses in the SQL.
*/
