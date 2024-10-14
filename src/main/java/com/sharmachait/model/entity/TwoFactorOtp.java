package com.sharmachait.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TwoFactorOtp {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private String id;

    private String otp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = WazirUser.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private WazirUser user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String jwt;
}
