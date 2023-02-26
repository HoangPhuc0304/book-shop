package com.hps.bookshop.security.oauth2;

import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.RefreshToken;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.service.refreshToken.RefreshTokenService;
import com.hps.bookshop.utils.JwtUtils;
import com.hps.bookshop.utils.RefreshTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenUtils refreshTokenUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //Save to security context holder
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println(authentication);

        //Set cookies for accessing resources
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtUtils.generateTokenFromId(userPrincipal.getId());
        User user = userRepository
                .findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new NotFoundException("Cannot find user with email: " +
                        userPrincipal.getEmail()));
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);

        String targetUrl = "/validate-token?accessToken=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
