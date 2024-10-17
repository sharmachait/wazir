package com.sharmachait.controller;

import com.sharmachait.config.JwtConstant;
import com.sharmachait.model.dto.UserDto;
import com.sharmachait.model.entity.VerificationCode;
import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.model.response.AuthResponse;
import com.sharmachait.repository.WazirUserRepository;
import com.sharmachait.service.EmailService;
import com.sharmachait.service.UserService.UserService;
import com.sharmachait.service.VerificationCodeService.VerificationCodeService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Data
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private WazirUserRepository wazirUserRepository;
    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getUserProfile(@RequestHeader(JwtConstant.JWT_HEADER) String jwtHeader, Authentication auth){
        try{
            WazirUser user =userService.findUserByJwt(jwtHeader);
            UserDto userDto = modelMapper.map(user, UserDto.class);
            System.out.println(auth.getName());
            System.out.println(auth.getAuthorities());
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            //handle
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad request");
        }
    }

    @PostMapping("/request/twofactorauth/{verificationType}")
    public ResponseEntity<String> requestTwoFactorAuth(
            @RequestHeader(JwtConstant.JWT_HEADER) String jwtHeader,
            @PathVariable VerificationType verificationType){
        try{
            WazirUser user =userService.findUserByJwt(jwtHeader);
//            UserDto userDto = modelMapper.map(user, UserDto.class);
            VerificationCode verificationCode = verificationCodeService
                    .getVerificationCodeByUserId(user.getId());
            if(verificationCode!=null){
                verificationCodeService.deleteVerificationCodeById(verificationCode.getId());
            }
            VerificationCode code = verificationCodeService.sendVerificationCode(user, verificationType);
           try{
               if(verificationType.equals(VerificationType.EMAIL)){
                   emailService.sendVerificationOtpEmail(user.getEmail(),code.getCode());
               }
           } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                       .body("Otp email not sent please try again");
           }
            return ResponseEntity.ok("Otp sent successfully!");
        } catch (Exception e) {
            //handle
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad request");
        }
    }

    @PatchMapping("/enable/twofactorauth/{otp}")
    public ResponseEntity<?> enableTwoFactorAuth(
            @RequestHeader(JwtConstant.JWT_HEADER) String jwtHeader,
            @PathVariable String otp){
        try{
            WazirUser user =userService.findUserByJwt(jwtHeader);
            VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());
            String sendTo;
            if(verificationCode.getVerificationType().equals(VerificationType.EMAIL)){
                sendTo = verificationCode.getEmail();
            }else{
                sendTo = verificationCode.getMobile();
            }
            boolean isVerified = verificationCodeService.verifyVerificationCode(verificationCode, otp);
            if(isVerified){

                WazirUser updatedUser = userService.enableTwoFactorAuthentication(
                        user,
                        verificationCode.getVerificationType(),
                        sendTo);
                verificationCodeService.deleteVerificationCodeById(verificationCode.getId());
            }
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            //handle
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad request");
        }
    }



}
