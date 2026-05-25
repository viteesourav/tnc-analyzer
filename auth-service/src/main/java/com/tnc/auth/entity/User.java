package com.tnc.auth.entity;

// Comes from spring-boot-starter-jpa
// Provide support for @Entity [tells Spring this class maps to database ], @ID [Mark primary key], @ GeneratedValue [Handles auto-increments], @Columns, @OneToMany etc
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    public User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}

/* 
    This above is ORM [Object Relationship Mapping] -> It acts as a bridge between 2 worlds i.e maps Java Objects directly to database table or vice-versa.

    Differences:
        1. ORM                          ->  Abstract idea of mapping Java class to database 
        2. JPA / jakarta.persistence    ->  The Official Java rulebook or standard blueprint that defines how ORM should look in java. It provides Rules & annotations like @Entity or @Id etc 
        3. Hibernate                    ->  The actual engine under the hood that does the heavy work writes SQL for use, and talk to database.
        4. Entity Class                 ->  A Simple java class decorated with @Entity -> represent a sinfle table inside the database.


*/ 
