package com.khattitoffe.WebBluej.service;

import com.khattitoffe.WebBluej.entity.UserData;
import com.khattitoffe.WebBluej.repository.DynamoDbRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    DynamoDbRepo dynamoDb;  

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
       UserData user;

       try {
          user = dynamoDb.getUserByEmail(email);
       }
       catch(Exception e)
       {
           throw new UsernameNotFoundException("Username not found (JWT)");
       }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

    }
}
