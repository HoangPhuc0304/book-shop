package com.hps.bookshop.utils;

import com.hps.bookshop.entity.UserPrincipal;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${jwt.secret.key}")
    private String jwtSecret;

    @Value("${jwt.expiration.time}")
    private int jwtExpirationMs;

    @Value("${jwt.cookie.name}")
    private String jwtCookie;

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public Cookie generateJwtCookie(UserPrincipal userPrincipal) {
        return generateJwtCookieFromId(userPrincipal.getId());
    }

    public Cookie generateJwtCookieFromId(Long id) {
        String jwt = generateTokenFromId(id);
        return generateCookieWithToken(jwt);
    }
    public Cookie generateCookieWithToken(String jwt) {
        Cookie cookie = new Cookie(jwtCookie, jwt);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie getCleanJwtCookie() {
        Cookie cookie = new Cookie(jwtCookie, null);
        return cookie;
    }

    public Long getIdFromJwtToken(String token) {
        return Long.parseLong(Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject());
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String generateTokenFromId(Long id) {
        return Jwts.builder()
                .setSubject(Long.toString(id))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean isExpiredJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }
}
