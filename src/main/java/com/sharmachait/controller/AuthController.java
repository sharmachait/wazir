package com.sharmachait.controller;

import com.sharmachait.config.JwtConstant;
import com.sharmachait.config.JwtProvider;
import com.sharmachait.model.dto.LoginDto;
import com.sharmachait.model.dto.RegisterDto;
import com.sharmachait.model.entity.*;
import com.sharmachait.model.response.AuthResponse;
import com.sharmachait.repository.WazirUserRepository;
import com.sharmachait.service.CustomUserDetailsService;
import com.sharmachait.service.EmailService;
import com.sharmachait.service.ForgotPasswordService.ForgotPasswordService;
import com.sharmachait.service.TwoFactorAuthService.TwoFactorOtpService;
import com.sharmachait.service.UserService.UserService;
import com.sharmachait.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private WazirUserRepository wazirUserRepository;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private TwoFactorOtpService twoFactorOtpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDto user) throws Exception {
        WazirUser alreadyExists = wazirUserRepository.findByEmail(user.getEmail());
        if(alreadyExists != null) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Email already exists");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(errorResponse);
        }
        WazirUser newUser = new WazirUser();
        newUser.setEmail(user.getEmail());
        //password encoding
        newUser.setPassword(encoder.encode(user.getPassword()));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",false,null));
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("User registered successfully");
//        authResponse.setSession();
//        authResponse.setTwoFactorAuthEnabled();
        WazirUser savedUser = wazirUserRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto user) throws Exception {
        WazirUser wazirUser = wazirUserRepository.findByEmail(user.getEmail());
        if(wazirUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",false,null));
        }

        Authentication auth;
        try{
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(),
                            user.getPassword())
            );
//            auth = authenticate(username, password);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null,false,"Unauthorized",false,null));
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt;
        try{
            jwt = JwtProvider.generateToken(auth);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",false,null));
        }
        if(wazirUser.getTwoFactorAuth().isEnabled()) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Two-factor authentication required");
            authResponse.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOtp();
            TwoFactorOtp old = twoFactorOtpService.findByUserId(wazirUser.getId());
            if(old!=null)
                twoFactorOtpService.deleteTwoFactorOtp(old);
            TwoFactorOtp newTwoFactorOtp = twoFactorOtpService.createTwoFactorOtp(wazirUser,otp,jwt);
            emailService.sendVerificationOtpEmail(wazirUser.getEmail(),otp);
            authResponse.setSession(newTwoFactorOtp.getId());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(authResponse);
        }
        else{
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(jwt);
            authResponse.setStatus(true);
            authResponse.setMessage("Logged in successfully");
//          authResponse.setSession();
//          authResponse.setTwoFactorAuthEnabled();
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        }

    }

    private Authentication authenticate(String username, String password) throws Exception {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if(userDetails == null) {
            throw new UsernameNotFoundException(username);
        }
        if(!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
    @PostMapping("/twoFactorAuth/{otp}")
    public ResponseEntity<AuthResponse> verifyTwoFactorOtp(
            @PathVariable String otp
            , @RequestParam String id
    )  {
        TwoFactorOtp twoFactorOtp = twoFactorOtpService.findById(id);
        if(twoFactorOtp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",false,null));
        }
        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOtp, otp)) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Two-factor authentication verified");
            authResponse.setTwoFactorAuthEnabled(true);
            authResponse.setJwt(twoFactorOtp.getJwt());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(authResponse);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null,false,"Unauthorized",false,null));
        }
    }

    @PostMapping("/forgotpassword/requestchange")
    public ResponseEntity<String> requestResetPassword(
            @RequestBody String email
            ){
        try{
            WazirUser user = userService.findUserByEmail(email);
            String otp = OtpUtils.generateOtp();
            String id = UUID.randomUUID().toString();
            String sendTo = user.getEmail();

            if(user.getTwoFactorAuth().getSendTo().equals(VerificationType.MOBILE))
                sendTo = user.getMobile();

            ForgotPasswordToken oldToken = forgotPasswordService.findByUserId(user.getId());
            if(oldToken!=null) {
                forgotPasswordService.deleteToken(oldToken);
            }
            ForgotPasswordToken token = forgotPasswordService.createToken(
                                                                user,
                                                                id,
                                                                otp,
                                                                user.getTwoFactorAuth().getSendTo(),
                                                                sendTo);
            if(user.getTwoFactorAuth().getSendTo().equals(VerificationType.EMAIL)) {
                emailService.sendVerificationOtpEmail(sendTo,otp);
            }
            return ResponseEntity.ok("Otp sent successfully!");
        } catch (Exception e) {
            //handle
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad request");
        }
    }

    @PatchMapping("/forgotpassword/verify")
    public ResponseEntity<String> verifyResetPassword(
            @RequestBody String email,
            @RequestBody String otp
    ){
        try{
            WazirUser user = userService.findUserByEmail(email);
            ForgotPasswordToken oldToken = forgotPasswordService.findByUserId(user.getId());
            if(oldToken==null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request");
            }

            boolean isVerified = forgotPasswordService.verifyForgotPasswordToken(oldToken,otp);

            if(isVerified) {
                return ResponseEntity.ok("Otp verified successfully!");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request");
            }
        } catch (Exception e) {
            //handle
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad request");
        }
    }

    @PatchMapping("/forgotpassword/reset")
    public ResponseEntity<String> resetPassword(
            @RequestBody String email,
            @RequestBody String newPassword,
            @RequestBody String otp
    ){
        try{
            WazirUser user = userService.findUserByEmail(email);
            ForgotPasswordToken oldToken = forgotPasswordService.findByUserId(user.getId());
            if(oldToken==null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request");
            }

            boolean isVerified = forgotPasswordService.verifyForgotPasswordToken(oldToken,otp);

            if(isVerified) {
                userService.updatePassword(user,newPassword);
                forgotPasswordService.deleteToken(oldToken);
                return ResponseEntity.ok("Otp verified successfully! Password updated!");
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad request");
            }
        } catch (Exception e) {
            //handle
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad request");
        }
    }
}
