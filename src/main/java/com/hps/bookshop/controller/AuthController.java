package com.hps.bookshop.controller;

import com.hps.bookshop.dto.LoginDto;
import com.hps.bookshop.dto.RegisterDto;
import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.service.AuthService;
import com.hps.bookshop.service.VerificationTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
            System.out.println("Ok");
            return "register";
        }
        try {
            System.out.println(registerDto);
            authService.register(registerDto, request);
            model.addAttribute("email", registerDto.getEmail());
            return "mail/activate-confirmation";
        } catch (Exception exc) {
            log.error(exc.getMessage());
            ObjectError objectError = new ObjectError("existed", exc.getMessage());
            bindingResult.addError(objectError);
            return "register";
        }
    }

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
        return "mail/activate-complete";
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

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }

    private String redirectToHomePage() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals(RoleType.VENDOR.name())) {
                return "redirect:/vendor";
            }
        }
        return "redirect:/";
    }
}
