package com.hps.bookshop.utils;

import com.hps.bookshop.model.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
@Slf4j
public class RefreshTokenUtils {
    @Value("${refreshToken.cookie.name}")
    private String refreshTokenCookie;

    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public Cookie generateRefreshTokenCookie(RefreshToken refreshToken) {
        Cookie cookie = new Cookie(refreshTokenCookie, refreshToken.getToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie getCleanJwtCookie() {
        Cookie cookie = new Cookie(refreshTokenCookie, null);
        return cookie;
    }
}
