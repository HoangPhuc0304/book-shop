package com.hps.bookshop.service.password;

import com.hps.bookshop.dto.ChangePasswordDto;
import com.hps.bookshop.exception.ExpirationTimeException;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.ChangePassword;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.ChangePasswordRepository;
import com.hps.bookshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class ChangePasswordServiceImpl implements ChangePasswordService{
    private final ChangePasswordRepository changePasswordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${change.password.expiration.time}")
    private int changePasswordExpirationTime;

    public ChangePasswordServiceImpl(ChangePasswordRepository changePasswordRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.changePasswordRepository = changePasswordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ChangePassword generateSessionByEmail(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cannot find user with email: " + email));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND,changePasswordExpirationTime);
        ChangePassword changePassword = new ChangePassword(
                UUID.randomUUID().toString(),
                new Date(),
                calendar.getTime(),
                user
        );
        return changePasswordRepository.save(changePassword);
    }

    @Override
    @Transactional
    public void updateNewPassword(String sessionId, ChangePasswordDto changePasswordDto) {
        ChangePassword changePassword = changePasswordRepository
                .findByCode(sessionId)
                .orElseThrow(()->new NotFoundException("Cannot find change password with code: "+sessionId));
        //If change password session has expired
        if (changePassword.getExpiryDate().getTime() <= new Date().getTime()) {
            throw new ExpirationTimeException("The time has expired. Expiry date: " +
                    changePassword.getExpiryDate() + ", checked date: " + new Date());
        }

        User user = changePassword.getUser();
        user.setPassword(passwordEncoder.encode(changePasswordDto.getPassword()));
        userRepository.save(user);
    }
}
