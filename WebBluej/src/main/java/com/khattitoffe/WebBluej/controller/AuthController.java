package com.khattitoffe.WebBluej.controller;
import com.khattitoffe.WebBluej.entity.AuthResponse;
import com.khattitoffe.WebBluej.entity.UserLogin;
import com.khattitoffe.WebBluej.service.JWTUtil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;
    
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@RequestBody UserLogin request) {

        //System.out.println("")

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtUtil.generateToken(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok().body(response);
        //return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            // validate token using your JWT utility
            if (jwtUtil.validateToken(token)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } 
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}