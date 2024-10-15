package com.sharmachait.service.UserService;

import com.sharmachait.config.JwtProvider;
import com.sharmachait.model.entity.TwoFactorAuth;
import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.repository.WazirUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private WazirUserRepository wazirUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public WazirUser findUserByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        WazirUser user = wazirUserRepository.findByEmail(email);
        if(user==null)
            throw new Exception("Invalid JWT token, wrong email claim");
        return user;
    }

    @Override
    public WazirUser findUserByEmail(String email) throws Exception {
        WazirUser user = wazirUserRepository.findByEmail(email);
        if(user==null)
            throw new Exception("Invalid email claim");
        return user;
    }

    @Override
    public WazirUser findUserById(Long id) throws NoSuchElementException {
        Optional<WazirUser> user = wazirUserRepository.findById(id);
        return user.get();
    }

    @Override
    public WazirUser enableTwoFactorAuthentication(
            WazirUser user,
            VerificationType verificationType,
            String sendTo
    ){
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);
        user.setTwoFactorAuth(twoFactorAuth);
        return wazirUserRepository.save(user);

    }

    @Override
    public WazirUser updatePassword(WazirUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return wazirUserRepository.save(user);
    }
}
