package com.hps.bookshop.security;

import com.hps.bookshop.config.SecurityConfig;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.RefreshToken;
import com.hps.bookshop.model.User;
import com.hps.bookshop.service.AuthService;
import com.hps.bookshop.service.RefreshTokenService;
import com.hps.bookshop.utils.JwtUtils;
import com.hps.bookshop.utils.RefreshTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenUtils refreshTokenUtils;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = jwtUtils.getJwtFromCookies(request);
            if (jwt != null && !jwt.isEmpty()) {
                //Get user from jwt token & save to security context holder
                if (jwtUtils.validateJwtToken(jwt)) {
                    retrieveAndSaveUserToSecurityContextHolder(jwt, request);
                }
                //Validate refresh token & generate a new jwt token
                else {
                    if (jwtUtils.isExpiredJwtToken(jwt)) {
                        String refreshTokenVal = refreshTokenUtils.getRefreshTokenFromCookies(request);
                        System.out.println(jwt);
                        System.out.println(refreshTokenVal);
                        boolean validate = refreshTokenService.validateRefreshToken(refreshTokenVal);
                        if (validate) {
                            RefreshToken refreshToken = refreshTokenService
                                    .findByToken(refreshTokenVal)
                                    .orElseThrow(() -> new NotFoundException("Cannot find refresh token with token: " +
                                            refreshTokenVal));
                            User user = refreshToken.getUser();
                            Cookie jwtCookie = jwtUtils.generateJwtCookieFromUsername(user.getUsername());
                            retrieveAndSaveUserToSecurityContextHolder(jwtCookie.getValue(), request);
                            response.addCookie(jwtCookie);
                        } else {
                            response.sendRedirect("/logout");
                            return;
                        }
                    }
                }
            } else {
                response.sendRedirect("/login");
                return;
            }
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean checkedFilter = Arrays.asList(SecurityConfig.WHITE_LIST_URLS).contains(path);
        System.out.println(path);
        System.out.println(checkedFilter);
        return checkedFilter;
    }

    private void retrieveAndSaveUserToSecurityContextHolder(String jwt, HttpServletRequest request) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info(authentication.toString());
    }
}
