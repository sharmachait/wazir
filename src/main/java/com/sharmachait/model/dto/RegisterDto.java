package com.sharmachait.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class RegisterDto {
    private String fullname;
    private String email;
    private String password;
    private String mobile;
}
