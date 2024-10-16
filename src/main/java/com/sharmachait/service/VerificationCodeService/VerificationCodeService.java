package com.sharmachait.service.VerificationCodeService;

import com.sharmachait.model.entity.VerificationCode;
import com.sharmachait.model.entity.VerificationType;
import com.sharmachait.model.entity.WazirUser;
import com.sharmachait.repository.VerificationCodeRepository;
import com.sharmachait.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class VerificationCodeService implements IVerificationCodeService {
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Override
    public VerificationCode sendVerificationCode(WazirUser user, VerificationType verificationType) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setVerificationType(verificationType);
        verificationCode.setCode(OtpUtils.generateOtp());
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws NoSuchElementException {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);

        return verificationCode.get();
    }

    @Override
    public VerificationCode getVerificationCodeByUserId(Long userId) {
        return verificationCodeRepository.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeById(Long id) {
        verificationCodeRepository.deleteById(id);
    }

    @Override
    public boolean verifyVerificationCode(VerificationCode verificationCode, String otp) {
        return verificationCode.getCode().equals(otp);
    }
}
