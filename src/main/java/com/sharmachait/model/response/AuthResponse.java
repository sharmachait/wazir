package com.sharmachait.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String jwt;
    private boolean status = false;
    private String message;
    private boolean isTwoFactorAuthEnabled = false;
    private String session = null;
}
