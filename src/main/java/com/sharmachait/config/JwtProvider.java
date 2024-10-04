package com.sharmachait.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;

public class JwtProvider {
    private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET.getBytes());
    public static String generateToken(String subject){
        Collection<? extends GrantedAuthority> authorities = auth
    }
}
