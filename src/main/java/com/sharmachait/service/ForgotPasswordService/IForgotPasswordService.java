package com.sharmachait.service.ForgotPasswordService;

import com.sharmachait.model.entity.ForgotPasswordToken;
import com.sharmachait.model.entity.VerificationCode;
import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;

public interface IForgotPasswordService {
    ForgotPasswordToken createToken(WazirUser user,
                                    String id,
                                    String code,
                                    VerificationType verificationType,
                                    String sendTo);
    ForgotPasswordToken findById(String id);
    ForgotPasswordToken findByUserId(Long userId);
    void deleteToken(ForgotPasswordToken token);
    boolean verifyForgotPasswordToken(ForgotPasswordToken forgotPasswordToken, String otp);
}
