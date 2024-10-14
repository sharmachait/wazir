package com.sharmachait.service;

import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//registering a bean of a service that implements the UserDetailsService
// tells the spring security framework to not use the default in memory user manager
//and use this instead, and there fore doesnt create a user at startup
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        WazirUser user = userRepository.findByEmail(username);
        if(user==null)
            throw new UsernameNotFoundException(username);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()+""));
        return new User(user.getEmail(),user.getPassword(),authorities);
    }
}
