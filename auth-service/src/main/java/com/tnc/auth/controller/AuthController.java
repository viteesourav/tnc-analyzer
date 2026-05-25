package com.tnc.auth.controller;

import com.tnc.auth.dto.LoginRequest;
import com.tnc.auth.dto.RegisterRequest;
import com.tnc.auth.entity.User;
import com.tnc.auth.repository.UserRepository;
import com.tnc.auth.util.JwtUtil;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // let's connect this with database using the Repository => Constructor type dependency injection
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Logic: For now, Fetch the user from DB if exists -> then check if the password matches [using BCrypt's from spring-security] -> return token if true.
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        // Check if the user exists in database...
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        //  if the user is present...
        if(optionalUser.isPresent()) {

            User user = optionalUser.get();

            // use BCrypt's password encoding to match and check the password.
            // Info:  BCrypt's hashes are Random every time, so matches internallt extract salt, hash input again and compare safely  [!! Industry-standard auth !!]
            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return JwtUtil.generateToken(user.getUsername());
            }
        }

        return "Invalid credentials";

    }

    // Logic: Take username/password from request => Encode the password => Save to database.
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        // Check if the user already exist in database for is a new user ?
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Username already exists";
        }

        // create a new user Object to save to db...
        User user = new User();

        user.setUsername(request.getUsername());
        
        // store the encoded password in the database...
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);

        return "User registered successfully";
    }
}
