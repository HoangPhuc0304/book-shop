package com.hps.bookshop.service;

import com.hps.bookshop.dto.LoginDto;
import com.hps.bookshop.dto.RegisterDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    void register(RegisterDto registerDto, HttpServletRequest request);

    void login(LoginDto loginDto, HttpServletResponse httpServletResponse);

    void logout(HttpServletResponse httpServletResponse);
}
