package com.hps.bookshop.security;

import com.hps.bookshop.config.SecurityConfig;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.RefreshToken;
import com.hps.bookshop.model.User;
import com.hps.bookshop.service.refreshToken.RefreshTokenService;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@NoArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenUtils refreshTokenUtils;
    @Autowired
    private UserDetailServiceImpl userDetailsService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = jwtUtils.getJwtFromCookies(request);
            System.out.println(jwt);
            if (jwt != null && !jwt.isEmpty()) {
                //Get user from jwt token & save to security context holder
                if (jwtUtils.validateJwtToken(jwt)) {
                    retrieveAndSaveUserToSecurityContextHolder(jwt, request);
                }
                //Validate refresh token & generate a new jwt token
                else {
                    if (jwtUtils.isExpiredJwtToken(jwt)) {
                        String refreshTokenVal = refreshTokenUtils.getRefreshTokenFromCookies(request);
                        boolean validate = refreshTokenService.validateRefreshToken(refreshTokenVal);
                        if (validate) {
                            RefreshToken refreshToken = refreshTokenService
                                    .findByToken(refreshTokenVal)
                                    .orElseThrow(() -> new NotFoundException("Cannot find refresh token with token: " +
                                            refreshTokenVal));
                            User user = refreshToken.getUser();
                            Cookie jwtCookie = jwtUtils.generateJwtCookieFromId(user.getId());
                            retrieveAndSaveUserToSecurityContextHolder(jwtCookie.getValue(), request);
                            response.addCookie(jwtCookie);
                        } else {
                            response.sendRedirect("/logout");
                            return;
                        }
                    }
                }
            }
            else {
                response.sendRedirect("/login");
                return;
            }
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        filterChain.doFilter(request, response);
    }

//    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.asList(SecurityConfig.WHITE_LIST_URLS).stream().anyMatch(x -> antPathMatcher.match(x,path));
    }

    private void retrieveAndSaveUserToSecurityContextHolder(String jwt, HttpServletRequest request) {
        Long userId = jwtUtils.getIdFromJwtToken(jwt);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info(authentication.toString());
    }
}
