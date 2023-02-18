package com.hps.bookshop.service;

import com.hps.bookshop.dto.LoginDto;
import com.hps.bookshop.dto.RegisterDto;
import com.hps.bookshop.entity.MailDetails;
import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.entity.UserDetailsImpl;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.RefreshToken;
import com.hps.bookshop.model.Role;
import com.hps.bookshop.entity.StatusAccount;
import com.hps.bookshop.model.User;
import com.hps.bookshop.model.VerificationToken;
import com.hps.bookshop.repository.RoleRepository;
import com.hps.bookshop.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenService verificationTokenService;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenUtils refreshTokenUtils;

    @Override
    @Transactional
    public void register(RegisterDto registerDto, HttpServletRequest request) throws UsernameNotFoundException {
        //register
        Optional<User> existedUser = userRepository.findByUsernameOrEmail(
                registerDto.getUsername(),
                registerDto.getEmail());
        if (existedUser.isPresent()) {
            throw new UsernameNotFoundException("Username or email already exists");
        }
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setCreatedDate(new Date());
        user.setStatus(StatusAccount.NOT_ACTIVATED);
        Role role;
        if (registerDto.getRole() != null) {
            role = roleRepository
                    .findByName(registerDto.getRole())
                    .orElseThrow(() -> new NotFoundException("Cannot find role with type: "
                            + registerDto.getRole().name()));
        } else {
            role = roleRepository.findByName(RoleType.CUSTOMER).get();
        }
        user.addRole(role);
        userRepository.save(user);

        //Create & save verify token
        VerificationToken token = verificationTokenService.generateVerificationToken(user);

        //send email to confirm
        mailService.sendSimpleEmail(MailDetails.builder()
                .recipient(user.getEmail())
                .subject("Please activate your account")
                .link(
                        ServletUriComponentsBuilder.fromRequestUri(request)
                                .replacePath(null)
                                .build()
                                .toUriString()
                                + "/verifyRegistration?token="
                                + token.getToken()
                ).build()
        );
    }


    @Override
    public void login(LoginDto loginDto, HttpServletResponse httpServletResponse) {
        //Authentication & save to security context holder
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Set cookies for accessing resources
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Cookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        User user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Cannot find user with username: " +
                        userDetails.getUsername()));
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
        Cookie refreshTokenCookie = refreshTokenUtils.generateRefreshTokenCookie(refreshToken);

        httpServletResponse.addCookie(jwtCookie);
        httpServletResponse.addCookie(refreshTokenCookie);
    }

    @Override
    public void logout(HttpServletResponse httpServletResponse) {
        Cookie jwtCookie = jwtUtils.getCleanJwtCookie();
        Cookie refreshTokenCookie = refreshTokenUtils.getCleanJwtCookie();
        httpServletResponse.addCookie(jwtCookie);
        httpServletResponse.addCookie(refreshTokenCookie);
    }
}
