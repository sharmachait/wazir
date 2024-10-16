package com.sharmachait.service.VerificationCodeService;

import com.sharmachait.model.entity.VerificationCode;
import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;

public interface IVerificationCodeService {
    VerificationCode sendVerificationCode(WazirUser user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id);
    VerificationCode getVerificationCodeByUserId(Long userId);
    void deleteVerificationCodeById(Long id);
    boolean verifyVerificationCode(VerificationCode verificationCode, String otp);
}
