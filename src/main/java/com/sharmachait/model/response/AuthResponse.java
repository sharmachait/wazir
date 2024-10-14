package com.sharmachait.model.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private boolean status = false;
    private String message;
    private boolean isTwoFactorAuthEnabled = false;
    private String session = null;
}
