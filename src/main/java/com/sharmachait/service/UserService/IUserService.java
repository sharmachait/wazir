package com.sharmachait.service.UserService;

import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;

public interface IUserService {
    public WazirUser findUserByJwt(String jwt) throws Exception;
    public WazirUser findUserByEmail(String email) throws Exception;
    public WazirUser findUserById(Long id);
    public WazirUser enableTwoFactorAuthentication(
            WazirUser user,
            VerificationType verificationType,
            String sendTo
    );
    public WazirUser updatePassword(WazirUser user, String newPassword);
}
