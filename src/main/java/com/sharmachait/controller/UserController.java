package com.sharmachait.controller;

import com.sharmachait.config.JwtConstant;
import com.sharmachait.model.dto.UserDto;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.service.EmailService;
import com.sharmachait.service.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @RequestMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestHeader(JwtConstant.JWT_HEADER) String jwtHeader){
        try{
            WazirUser user =userService.findUserByJwt(jwtHeader);
        } catch (Exception e) {
            //handle
            throw new RuntimeException(e);
        }
    }
}
