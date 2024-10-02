package com.sharmachait.controller;

import com.sharmachait.model.WazirUser;
import com.sharmachait.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WazirUser> register(@RequestBody WazirUser user) {
        try{
            WazirUser newUser = new WazirUser();
            newUser.setEmail(user.getEmail());
            newUser.setPassword(user.getPassword());
            newUser.setEmail(user.getEmail());
            newUser.setFullname(user.getFullname());
            WazirUser savedUser = userRepository.save(newUser);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        }catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
