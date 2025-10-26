package com.khattitoffe.WebBluej.service;
import java.util.Date;
import com.khattitoffe.WebBluej.repository.DynamoDbRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
@Component
public class JWTUtil {

    @Autowired
    private DynamoDbRepo dynamoDB;

    private String secretKey="myapplication@4bahujd*(@!6ye9182";
    private long expiration =1000*60*60*12;

    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token,String email){
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    public boolean validateToken(String token){
        String email=extractEmail(token);
        return dynamoDB.userExists(email);
    }


}
