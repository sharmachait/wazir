package com.sharmachait.service.TwoFactorAuthService;

import com.sharmachait.model.entity.TwoFactorOtp;
import com.sharmachait.model.entity.WazirUser;

public interface ITwoFactorOtpService {
    TwoFactorOtp createTwoFactorOtp(WazirUser user, String otp, String jwt);
    TwoFactorOtp findByUserId(Long userId);
    TwoFactorOtp findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOtp twoFactorOtp, String otp);
    void deleteTwoFactorOtp(TwoFactorOtp twoFactorOtp);
}
