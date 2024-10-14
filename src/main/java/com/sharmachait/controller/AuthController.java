package com.sharmachait.controller;

import com.sharmachait.config.JwtProvider;
import com.sharmachait.model.response.ApiResponse;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.model.response.AuthResponse;
import com.sharmachait.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody WazirUser user) throws Exception {
        WazirUser alreadyExists = userRepository.findByEmail(user.getEmail());
        if(alreadyExists != null) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Email already exists");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);
        }
        WazirUser newUser = new WazirUser();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setFullname(user.getFullname());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(newUser.getEmail(), newUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt;
        try{
             jwt = JwtProvider.generateToken(auth);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("User registered successfully");
//        authResponse.setSession();
//        authResponse.setTwoFactorAuthEnabled();
        WazirUser savedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
}
