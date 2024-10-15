package com.sharmachait.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtProvider {
    private static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET.getBytes());
    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("authorities", roles)// extracted in the filter
                .claim("email",auth.getName())// extracted in the filter
                .signWith(key)
                .compact();

        return jwt;
    }
    public static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }
    public static String getEmailFromToken(String token) throws Exception {
        if(token==null || token.length() < 8)
            throw new Exception("Invalid JWT token");
        String jwt = token.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(JwtConstant.JWT_SECRET.getBytes());
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
            String email = String.valueOf(claims.get("email"));
            return email;
        } catch (Exception e) {
            throw new Exception(e);
        }

    }
}
