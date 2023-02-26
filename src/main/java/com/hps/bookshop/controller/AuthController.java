package com.hps.bookshop.controller;

import com.hps.bookshop.dto.*;
import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.model.ChangePassword;
import com.hps.bookshop.service.auth.AuthService;
import com.hps.bookshop.service.password.ChangePasswordService;
import com.hps.bookshop.service.verification.VerificationCodeService;
import com.hps.bookshop.service.verification.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final VerificationTokenService verificationTokenService;
    private final VerificationCodeService verificationCodeService;
    private final ChangePasswordService changePasswordService;

    //Register, login, logout
    @GetMapping("/register")
    public String register(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("user", registerDto);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid RegisterDto registerDto, BindingResult bindingResult
            , Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            authService.register(registerDto, request);
            model.addAttribute("email", registerDto.getEmail());
            return "services/mail/activate-confirmation";
        } catch (Exception exc) {
            log.error(exc.getMessage());
            ObjectError objectError = new ObjectError("existed", exc.getMessage());
            bindingResult.addError(objectError);
            return "register";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute("user", registerDto);
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") @Valid LoginDto loginDto, BindingResult bindingResult,
                        HttpServletResponse httpServletResponse, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "login";
            }
            authService.login(loginDto, httpServletResponse);
            return redirectToHomePage();
        } catch (Exception exc) {
            log.error(exc.getMessage());
            ObjectError objectError = new ObjectError("error", exc.getMessage());
            bindingResult.addError(objectError);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse httpServletResponse) {
        System.out.println("Try to logout");
        authService.logout(httpServletResponse);
        return "redirect:/login";
    }

    //Supported features
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token, Model model, HttpServletRequest request) {
        try {
            boolean result = verificationTokenService.validateVerificationToken(token, request);
            if (result) {
                model.addAttribute("message", "Validate complete! Please go back to login page.");
            } else {
                model.addAttribute("message", "Validate failed! The validation has expired, " +
                        "a new verification token send to your email.");
            }
        } catch (Exception exc) {
            log.error(exc.getMessage());
            model.addAttribute("message", "Validate failed! " + exc.getMessage() + ", " +
                    "please register again.");
        }
        return "services/mail/activate-complete";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(Model model) {
        IdentifyEmail identifyEmail = new IdentifyEmail();
        model.addAttribute("identifyEmail", identifyEmail);
        return "services/supports/forgot-password";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassword(@ModelAttribute("identifyEmail") IdentifyEmail identifyEmail, BindingResult bindingResult,
                                 HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "services/supports/forgot-password";
        }
        try {
            //Send code to email
            verificationCodeService.sendCode(identifyEmail.getEmail(), request);
            return "redirect:/confirmCode?email=" + identifyEmail.getEmail();
        } catch (Exception exc) {
            log.error(exc.getMessage());
            ObjectError objectError = new ObjectError("error", exc.getMessage());
            bindingResult.addError(objectError);
            return "services/supports/forgot-password";
        }
    }

    @GetMapping("/confirmCode")
    public String confirmCode(@RequestParam("email") String email, Model model) {
        IdentifyCode identifyCode = new IdentifyCode(email, null);
        model.addAttribute("identifyCode", identifyCode);
        return "services/supports/identity-code";
    }

    @PostMapping("/confirmCode")
    public String confirmCode(@ModelAttribute("identifyCode") IdentifyCode identifyCode, Model model,
                              HttpServletRequest request, BindingResult bindingResult) {
        String email = identifyCode.getEmail();
        String code = identifyCode.getCode();
        //Verify code
        try {
            boolean result = verificationCodeService.validateVerificationCode(email, code, request);
            if (result) {
                ChangePassword changePassword = changePasswordService.generateSessionByEmail(email);
                return "redirect:/changePassword?sessionId=" + changePassword.getCode();
            }
        } catch (Exception exc) {
            log.error(exc.getMessage());
            ObjectError objectError = new ObjectError("error", exc.getMessage());
            bindingResult.addError(objectError);
        }
        return "services/supports/identity-code";
    }

    @GetMapping("/changePassword")
    public String changePassword(@RequestParam("sessionId") String sessionId, Model model) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setSessionId(sessionId);
        model.addAttribute("changePassword", changePasswordDto);
        return "services/supports/change-password";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("changePassword") @Valid ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult) {
        String sessionId = changePasswordDto.getSessionId();
        System.out.println("OK");
        if (bindingResult.hasErrors()) {
            return "services/supports/change-password";
        }
        try {
            //Save to db
            changePasswordService.updateNewPassword(sessionId, changePasswordDto);
            return "redirect:/login";
        } catch (Exception exc) {
            log.error(exc.getMessage());
            ObjectError objectError = new ObjectError("error", exc.getMessage());
            bindingResult.addError(objectError);
            return "services/supports/change-password";
        }
    }

    @GetMapping("/validate-token")
    public String validateToken(@RequestParam("accessToken") String accessToken, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            if (authService.validateAccessToken(accessToken, request, response)) {
                return redirectToHomePage();
            } else {
                log.error("Not valid");
                return "redirect:/";
            }
        } catch (Exception exc) {
            log.error(exc.getMessage());
            return "redirect:/";
        }
    }

    private String redirectToHomePage() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals(RoleType.VENDOR.name())) {
                return "redirect:/vendor";
            }
        }
        return "redirect:/";
    }
}
