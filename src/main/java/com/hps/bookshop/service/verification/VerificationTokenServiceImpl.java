package com.hps.bookshop.service.verification;

import com.hps.bookshop.entity.MailDetails;
import com.hps.bookshop.entity.StatusAccount;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.User;
import com.hps.bookshop.model.VerificationToken;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.repository.VerificationTokenRepository;
import com.hps.bookshop.service.mail.MailContentBuilder;
import com.hps.bookshop.service.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;
    @Value("${activate.account.expiration.time}")
    private int activateAccountExpirationTime;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository,
                                        UserRepository userRepository, MailService mailService,
                                        MailContentBuilder mailContentBuilder) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.mailContentBuilder = mailContentBuilder;
    }

    @Override
    @Transactional
    public void sendToken(User user, HttpServletRequest request) {
        //Create & save verify token
        VerificationToken token = generateVerificationToken(user);

        //send email to confirm
        mailService.sendSimpleEmail(MailDetails.builder()
                .recipient(user.getEmail())
                .subject("Please activate your account")
                .text(mailContentBuilder.buildVerificationToken(
                        ServletUriComponentsBuilder.fromRequestUri(request)
                                .replacePath(null)
                                .build()
                                .toUriString()
                                + "/verifyRegistration?token="
                                + token.getToken())
                )
                .isHtml(true)
                .build()
        );
    }

    @Override
    @Transactional
    public boolean validateVerificationToken(String token, HttpServletRequest request) {
        //Check if verification token exists
        VerificationToken verificationToken = verificationTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new NotFoundException("Cannot find verification token with token: " + token));

        User user = verificationToken.getUser();

        //If verification token has expired, a new token will send to email
        if (verificationToken.getExpiryDate().getTime() <= new Date().getTime()) {
            sendToken(user,request);
            return false;
        }

        user.setStatus(StatusAccount.ACTIVATED);
        userRepository.save(user);
        verificationTokenRepository.deleteByUser(user);
        return true;
    }

    @Transactional
    private VerificationToken generateVerificationToken(User user) {
        //Delete old verification token if exist
        verificationTokenRepository.deleteByUser(user);

        //generate a new verification token
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, activateAccountExpirationTime);
        verificationToken.setExpiryDate(calendar.getTime());

        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
}
