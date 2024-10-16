package com.sharmachait.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String code;
    @OneToOne(fetch = FetchType.EAGER, targetEntity = WazirUser.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private WazirUser user;
    private String email;
    private String mobile;
    private VerificationType verificationType;
}
