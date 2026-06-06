package com.tnc.entity;

// Comes from spring-boot-starter-jpa
// Provide support for @Entity [tells Spring this class maps to database ], @ID [Mark primary key], @ GeneratedValue [Handles auto-increments], @Columns, @OneToMany etc
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name= "analysis_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer safetyScore;

    @Column(length = 2000)
    private String summary;

    @Enumerated(EnumType.STRING)
    private AnalysisSource source;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
}

/*
    Note:
        -- we have this entity structure that represent's the Analysis storage table in database.
        -- To keep thing consistent, we have addded Eunm for soruce and status.
            -> NOTE: we have use @Enumerated(EnumType.STRING)
            -> This makes sure, we take the String value of the items defined in Eumn class --> If not included then Data-inconsistency may arise.
        -- @Builder annotation:
            This allows us to build the Result DTO like this:
                AnalysisResult.builder()
                            .username(username)
                            .title(title)
                            .summary(summary)
                            .build();
        -- username is stored here sperately.
            NOTE: auth-service refers to "User" table in backend --> but Analysis_result table should not refer to the same table.
                Reason: In Micro-service architecture, each services should connect to it's own tables, that way in future if auth-service database moved
                        from Postgres to somewhere else, it should not in any way effect Analysis_result.
                    --> This set's Microservices main concept -> where each service operate independently !

*/
