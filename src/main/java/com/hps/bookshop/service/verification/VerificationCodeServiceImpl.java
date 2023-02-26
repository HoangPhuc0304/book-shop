package com.hps.bookshop.service.verification;

import com.hps.bookshop.entity.MailDetails;
import com.hps.bookshop.exception.ExpirationTimeException;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.exception.NotMatchException;
import com.hps.bookshop.model.User;
import com.hps.bookshop.model.VerificationCode;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.repository.VerificationCodeRepository;
import com.hps.bookshop.service.mail.MailContentBuilder;
import com.hps.bookshop.service.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;
    @Value("${verify.code.expiration.time}")
    private int verifyCodeExpirationTime;

    public VerificationCodeServiceImpl(VerificationCodeRepository verificationCodeRepository,
                                       UserRepository userRepository,MailService mailService,
                                       MailContentBuilder mailContentBuilder) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.mailContentBuilder = mailContentBuilder;
    }

    @Override
    @Transactional
    public void sendCode(String email, HttpServletRequest request) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cannot find user with email: " + email));
        //Create & save verify token
        VerificationCode code = generateCode(user);

        //send email to confirm
        mailService.sendSimpleEmail(MailDetails.builder()
                .recipient(user.getEmail())
                .subject("Please confirm your code for updating password")
                .text(mailContentBuilder.buildVerificationCode(code.getCode()))
                .isHtml(true)
                .build()
        );
    }

    @Override
    @Transactional
    public boolean validateVerificationCode(String email, String code, HttpServletRequest request) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cannot find user with email: " + email));
        //Check if verification code exists
        VerificationCode verificationCode = verificationCodeRepository
                .findByUser(user)
                .orElseThrow(() -> new NotFoundException("Cannot find verification code with email: " +
                        user.getEmail()));
        //If verification code is incorrect
        if (!verificationCode.getCode().equals(code)) {
            throw new NotMatchException("The code doesn't match");
        }
        //If verification code has expired
        if (verificationCode.getExpiryDate().getTime() <= new Date().getTime()) {
            throw new ExpirationTimeException("The time has expired. Expiry date: " +
                    verificationCode.getExpiryDate() + ", checked date: " + new Date());
        }

        verificationCodeRepository.deleteByUser(user);
        return true;
    }

    @Transactional
    private VerificationCode generateCode(User user) {
        //Delete old verification code if exist
        verificationCodeRepository.deleteByUser(user);

        //generate a new verification code
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(Long.toString(randomCode(100000, 999999)));
        verificationCode.setUser(user);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, verifyCodeExpirationTime);
        verificationCode.setExpiryDate(calendar.getTime());

        verificationCodeRepository.save(verificationCode);
        return verificationCode;
    }

    private long randomCode(int min, int max) {
        return Math.round(Math.random() * (max - min + 1) + min);
    }
}
