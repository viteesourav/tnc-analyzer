package com.tnc.auth.repository;

import com.tnc.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}

/*
    The above handles the SQL queires for us automatically. Thats the advantage of extending the JpaRepository.
    Spring Data JPA generates the query and fetch the details.

*/