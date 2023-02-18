package com.hps.bookshop.service;

import com.hps.bookshop.model.User;
import com.hps.bookshop.model.VerificationToken;
import jakarta.servlet.http.HttpServletRequest;

public interface VerificationTokenService {
    VerificationToken generateVerificationToken(User user);
    boolean validateVerificationToken(String token, HttpServletRequest request);
}
