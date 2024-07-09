package com.mrthinkj.securityservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expiration;

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String generateToken(String username){
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime()+expiration);
        return Jwts.builder()
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .setSubject(username)
                .signWith(key())
                .compact();
    }
}
