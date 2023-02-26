package com.hps.bookshop.service.verification;

import com.hps.bookshop.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface VerificationTokenService {
    void sendToken(User user, HttpServletRequest request);
    boolean validateVerificationToken(String token, HttpServletRequest request);
}
