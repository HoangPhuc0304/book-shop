package com.hps.bookshop.controller;

import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.model.Role;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.RoleRepository;
import com.hps.bookshop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/test1/{username}")
    public ResponseEntity<?> showUser(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username).get();
        Role role = roleRepository.findByName(RoleType.CUSTOMER).get();
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    @GetMapping("/test2/{roleId}")
    public ResponseEntity<?> showRole(@PathVariable("roleId") String roleId) {
        try {
            Role role = roleRepository.findByName(RoleType.CUSTOMER).get();
            return ResponseEntity.status(HttpStatus.OK).body(role);
        } catch (Exception exc) {
            log.error(exc.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(exc.getMessage());
        }
    }
}
