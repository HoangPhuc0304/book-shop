package com.hps.bookshop.controller;

import com.hps.bookshop.dto.*;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.service.book.BookService;
import com.hps.bookshop.service.shop.ShopService;
import com.hps.bookshop.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@AllArgsConstructor
public class ShopController {
    private final UserService userService;
    private final ShopService shopService;
    private final BookService bookService;

    @GetMapping({"/shops", "/vendor/shops"})
    public String getAllShops(@RequestParam(value = "userId",required = false) String userId, Model model) {
        try {
            List<ShopResponseDto> shops;
            if (userId == null || userId.isEmpty()) {
                shops = shopService.getAllShops();
            } else {
                shops = shopService.getAllShops(Long.parseLong(userId));
                UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();
                Long currentUserId = userPrincipal.getId();
                if(currentUserId == Long.parseLong(userId)) {
                    model.addAttribute("userId", userId);
                    model.addAttribute("isAuthorize", true);
                }
            }
            model.addAttribute("shops", shops);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "shop/list";
    }

    @GetMapping({"/shops/{shopId}", "/vendor/shops/{shopId}"})
    public String getShop(@PathVariable("shopId") String shopId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
            UserResponseDto userResponseDto = userService.showInfo(shopResponseDto.getUserId());
            List<BookResponseDto> books = bookService.getBooksFromShop(Long.parseLong(shopId));

            if(shopResponseDto.getUserId().equals(userPrincipal.getId())) {
                model.addAttribute("isAuthorize",true);
            }
            model.addAttribute("shop", shopResponseDto);
            model.addAttribute("user", userResponseDto);
            model.addAttribute("shopId", shopId);
            model.addAttribute("books", books);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "shop/view";
    }
}
