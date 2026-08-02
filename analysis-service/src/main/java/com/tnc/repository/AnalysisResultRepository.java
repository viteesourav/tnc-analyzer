package com.tnc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tnc.entity.AnalysisResult;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long>, JpaSpecificationExecutor<AnalysisResult> {
    // JAP Specification: Now have access to findAll(Specification, pageable)

    // Fetch Entire history timeline for a single user sorted by newest first.
    // Update: below method removed -> replaced with JpaSpecification
    // Page<AnalysisResult> findByUsername(String username, Pageable pageable);

    // Analyses Stats API's data fetching....=> 2 ways: Derived queries + JPQL queries [Efficient choice]

    // @method1: Fetches total analysis by usename.
    long countByUsername(String username);

    // @method2: Fetches by user and then count rows between the min-max score limits.
    long countByUsernameAndSafetyScoreBetween(
        String username,
        Integer minScore,
        Integer maxScore
    );


    // @method3: Fetches all records by username -> then takes average score of all rows.
    // But wait, we don't have any average functionality in derived method -> count, exists, delete exists but not Avg()
    // Sol: use JPQL here i.e cutsom queries on JPA entity.
    @Query("""
           SELECT AVG(a.safetyScore)
           FROM AnalysisResult a
           WHERE a.username = :username     
        """)
    Double findAverageSafetyScoreByUsername(
        @Param("username") String username
    );
    
    // @method4: fetching all records by id and username -> This is a simple derived query.
    // using Option<> here, as the selection might return with 0 rows.
    Optional<AnalysisResult> findByIdAndUsername(
        Long id,
        String username
    );

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

    Enhancement:
        -- Adding pagination support using Pageable from org.springframework.data.domain.
        -- Spring data JPA, understands pageable and automatically adds LIMTI and OFFEST clauses in the SQL.
    
    Enhancement:
        -- Instead of declaring derived query methods for Operations including filtering by username or riskLevel + searching.
        -- We can move with JPA Specifications.
            - Very refined way to build and combine filters as required without calling lot of methods.
        -- Adding derived Queries  + JPQL queries for fetching data for Analysis Stats API.
*/
