package com.fnb.backend.security;

import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TokenProvider {
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generate(String encryptData) {

        Date issuedAt   = new Date();
        Date expiration = new Date(issuedAt.getTime() + TimeUnit.HOURS.toMillis(1));

        return Jwts.builder()
                .subject(encryptData)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(this.secretKey, Jwts.SIG.HS256)
                .compact();
    }
}