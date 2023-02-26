package com.hps.bookshop.service.auth;

import com.hps.bookshop.dto.LoginDto;
import com.hps.bookshop.dto.RegisterDto;
import com.hps.bookshop.entity.AuthProvider;
import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.*;
import com.hps.bookshop.entity.StatusAccount;
import com.hps.bookshop.repository.RoleRepository;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.service.refreshToken.RefreshTokenService;
import com.hps.bookshop.service.verification.VerificationTokenService;
import com.hps.bookshop.utils.JwtUtils;
import com.hps.bookshop.utils.RefreshTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenUtils refreshTokenUtils;

    @Override
    @Transactional
    public void register(RegisterDto registerDto, HttpServletRequest request) throws NotFoundException {
        //Check if username or email existed
        Optional<User> existedUser = userRepository.findByEmail(registerDto.getEmail());
        if (existedUser.isPresent()) {
            throw new NotFoundException("Email already exists");
        }

        //Save user to db
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        user.setCreatedDate(new Date());
        user.setStatus(StatusAccount.NOT_ACTIVATED);
        Role role;
        if (registerDto.getRole() != null) {
            role = roleRepository
                    .findByName(registerDto.getRole())
                    .orElseThrow(() -> new NotFoundException("Cannot find role with type: "
                            + registerDto.getRole().name()));
        } else {
            role = roleRepository
                    .findByName(RoleType.CUSTOMER)
                    .orElseThrow(() -> new NotFoundException("Cannot find role type: " + RoleType.CUSTOMER));
        }
        user.addRole(role);
        userRepository.save(user);

        //Send verification token to email to activate account
        verificationTokenService.sendToken(user, request);
    }


    @Override
    public void login(LoginDto loginDto, HttpServletResponse response) {
        //Authentication & save to security context holder
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Set cookies for accessing resources
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Cookie jwtCookie = jwtUtils.generateJwtCookie(userPrincipal);
        User user = userRepository
                .findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new NotFoundException("Cannot find user with email: " +
                        userPrincipal.getEmail()));
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
        Cookie refreshTokenCookie = refreshTokenUtils.generateRefreshTokenCookie(refreshToken);

        response.addCookie(jwtCookie);
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie jwtCookie = jwtUtils.getCleanJwtCookie();
        Cookie refreshTokenCookie = refreshTokenUtils.getCleanJwtCookie();
        response.addCookie(jwtCookie);
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public boolean validateAccessToken(String accessToken, HttpServletRequest request, HttpServletResponse response) {
        if (jwtUtils.validateJwtToken(accessToken)) {
            Long userId = jwtUtils.getIdFromJwtToken(accessToken);
            User user = userRepository
                    .findById(userId)
                    .orElseThrow(() -> new NotFoundException("Cannot find user with id: " + userId));

            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            RefreshToken refreshToken = refreshTokenService
                    .findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Cannot find refresh token with user: " + userId));
            Cookie jwtCookie = jwtUtils.generateCookieWithToken(accessToken);
            Cookie refreshTokenCookie = refreshTokenUtils.generateRefreshTokenCookie(refreshToken);

            response.addCookie(jwtCookie);
            response.addCookie(refreshTokenCookie);
            return true;
        }
        return false;
    }
}
