package com.sharmachait.model.dto;

import com.sharmachait.model.entity.TwoFactorAuth;
import com.sharmachait.model.entity.USER_ROLE;

public class UserDto {
    private Long id;
    private String fullname;
    private String email;
    private String mobile;
    private USER_ROLE role;
    private TwoFactorAuth twoFactorAuth;
}
