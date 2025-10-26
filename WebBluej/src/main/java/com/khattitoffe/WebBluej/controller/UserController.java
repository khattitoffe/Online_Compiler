package com.khattitoffe.WebBluej.controller;
import com.khattitoffe.WebBluej.entity.UserData;
import com.khattitoffe.WebBluej.entity.UserLogin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.khattitoffe.WebBluej.service.UserEntry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserEntry db;

    @PostMapping("/signUp")
    public ResponseEntity<?> createUser(@RequestBody UserData user)
    {
        if(!db.verifyEmail((user))) // verrifies email and checks if its already in db or not
           {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid email");
            return ResponseEntity.badRequest().body(response);
            }

        if(db.saveUser(user))
        {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User Created " + user.getUsername());
            return ResponseEntity.ok(response);
        }
        else
        {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User already exists");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin user)
    {
        if(db.userExists(user))
           { 
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login Successful");
            return ResponseEntity.ok(response);
         }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Invalid Username or Password");
        return ResponseEntity.badRequest().body(response);
        //return ResponseEntity.badRequest().body("Invalid username or password");
    }
}