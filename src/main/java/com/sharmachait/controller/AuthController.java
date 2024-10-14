package com.sharmachait.controller;

import com.sharmachait.config.JwtProvider;
import com.sharmachait.model.dto.LoginDto;
import com.sharmachait.model.dto.RegisterDto;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.model.response.AuthResponse;
import com.sharmachait.repository.UserRepository;
import com.sharmachait.service.CustomUserDetailsService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder encoder;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDto user) throws Exception {
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

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto user) throws Exception {
        String username = user.getEmail();
        String password = user.getPassword();

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
            throw new RuntimeException(e);
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Logged in successfully");
//        authResponse.setSession();
//        authResponse.setTwoFactorAuthEnabled();
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
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
}
