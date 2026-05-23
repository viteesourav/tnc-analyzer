package com.tnc.auth.controller;

import com.tnc.auth.dto.LoginRequest;
import com.tnc.auth.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        if("admin".equals(request.getUsername())
            && "password".equals(request.getPassword())) {
        
            return JwtUtil.generateToken(request.getUsername());
        }

        return "Invalid credentials";   // fall-back default response !

    }


}
