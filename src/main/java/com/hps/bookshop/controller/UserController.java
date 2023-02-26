package com.hps.bookshop.controller;

import com.hps.bookshop.dto.UserDetailsResponseDto;
import com.hps.bookshop.dto.UserEditDto;
import com.hps.bookshop.dto.UserResponseDto;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotMatchException;
import com.hps.bookshop.service.file.FileStorageService;
import com.hps.bookshop.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Controller
@AllArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @GetMapping("")
    public String showInfo() {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            System.out.println(currentUserId);
            return "redirect:/users/" + currentUserId;
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String showInfo(@PathVariable("id") String userId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            if (Long.parseLong(userId) == currentUserId) {
                UserDetailsResponseDto userResponseDto = userService.showDetailsInfo(userPrincipal.getId());
                model.addAttribute("user", userResponseDto);
                return "user/view-detail";
            } else {
                UserResponseDto userResponseDto = userService.showInfo(Long.parseLong(userId));
                model.addAttribute("user", userResponseDto);
                return "user/view";
            }
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") String userId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            if (Long.parseLong(userId) != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            UserDetailsResponseDto userDetailsResponseDto = userService.showDetailsInfo(Long.parseLong(userId));
            UserEditDto userEditDto = userService.showEditUser(Long.parseLong(userId));
            model.addAttribute("info", userDetailsResponseDto);
            model.addAttribute("user", userEditDto);
            return "user/edit";
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable("id") String userId, @ModelAttribute("user") @Valid UserEditDto userEditDto,
                       BindingResult bindingResult, Model model) {
        try {
            System.out.println(userEditDto);
            UserDetailsResponseDto userDetailsResponseDto = userService.showDetailsInfo(Long.parseLong(userId));
            model.addAttribute("info", userDetailsResponseDto);
            if (bindingResult.hasErrors()) {
                System.out.println(bindingResult.getAllErrors().get(0).getDefaultMessage());
                return "user/edit";
            }
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            if (Long.parseLong(userId) != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            userService.updateEditUser(Long.parseLong(userId),userEditDto);
            return "redirect:/users";
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor";
    }
}
