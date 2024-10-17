package com.sharmachait.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String code;
    @OneToOne(fetch = FetchType.EAGER, targetEntity = WazirUser.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private WazirUser user;
    private String sendTo;
    private VerificationType verificationType;
}
