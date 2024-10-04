package com.sharmachait.controller;

import com.sharmachait.model.WazirUser;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<WazirUser> register(@RequestBody WazirUser user) {
        WazirUser alreadyExists = userRepository.findByEmail(user.getEmail());
        if(alreadyExists != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        WazirUser newUser = new WazirUser();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        newUser.setFullname(user.getFullname());
        WazirUser savedUser = userRepository.save(newUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(newUser.getEmail(), newUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
