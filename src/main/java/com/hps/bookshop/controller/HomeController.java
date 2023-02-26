package com.hps.bookshop.controller;

import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.model.Role;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.RoleRepository;
import com.hps.bookshop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Controller
@AllArgsConstructor
@Slf4j
public class HomeController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/")
    public String showHome() {
        return "customer/index";
    }

    @GetMapping("/vendor")
    public String showHomeVendor() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long currentUserId = userPrincipal.getId();
        return "redirect:/vendor/shops?userId=" + currentUserId;
    }
}
