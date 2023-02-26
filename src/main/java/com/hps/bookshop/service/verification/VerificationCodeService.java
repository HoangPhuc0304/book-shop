package com.hps.bookshop.service.verification;

import jakarta.servlet.http.HttpServletRequest;

public interface VerificationCodeService {
    void sendCode(String email, HttpServletRequest request);
    boolean validateVerificationCode(String email, String code, HttpServletRequest request);
}
