package com.sharmachait.repository;

import com.sharmachait.model.entity.TwoFactorOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOtp, String> {
    TwoFactorOtp findByUserId(Long userId);
}
