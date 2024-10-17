package com.sharmachait.service.ForgotPasswordService;

import com.sharmachait.model.entity.ForgotPasswordToken;
import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.repository.ForgotPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ForgotPasswordService implements IForgotPasswordService {
    @Autowired
    private ForgotPasswordTokenRepository forgotPasswordTokenRepository;
    @Override
    public ForgotPasswordToken createToken(WazirUser user, String id, String code, VerificationType verificationType, String sendTo) {
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setSendTo(sendTo);
        token.setVerificationType(verificationType);
        token.setCode(code);
        token.setId(id);
        return forgotPasswordTokenRepository.save(token);
    }

    @Override
    public ForgotPasswordToken findById(String id) throws NoSuchElementException {
        Optional<ForgotPasswordToken> optional = forgotPasswordTokenRepository.findById(id);
        return optional.get();
    }

    @Override
    public ForgotPasswordToken findByUserId(Long userId) {
        return forgotPasswordTokenRepository.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordTokenRepository.delete(token);
    }

    @Override
    public boolean verifyForgotPasswordToken(ForgotPasswordToken forgotPasswordToken, String otp) {
        return forgotPasswordToken.getCode().equals(otp);
    }
}
