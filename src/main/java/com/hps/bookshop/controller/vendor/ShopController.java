package com.hps.bookshop.controller.vendor;

import com.hps.bookshop.dto.*;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotMatchException;
import com.hps.bookshop.service.book.BookService;
import com.hps.bookshop.service.shop.ShopService;
import com.hps.bookshop.service.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
            }
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            userId = currentUserId.toString();
            model.addAttribute("userId", userId);
            model.addAttribute("shops", shops);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "vendor/shop/list";
    }

    @GetMapping({"/shops/{shopId}", "/vendor/shops/{shopId}"})
    public String getShop(@PathVariable("shopId") String shopId, Model model) {
        try {
            ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
            UserResponseDto userResponseDto = userService.showInfo(shopResponseDto.getUserId());
            List<BookResponseDto> books = bookService.getBooksFromShop(Long.parseLong(shopId));

            model.addAttribute("shop", shopResponseDto);
            model.addAttribute("user", userResponseDto);
            model.addAttribute("shopId", shopId);
            model.addAttribute("books", books);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "vendor/shop/view";
    }

    @GetMapping("/vendor/shops/add")
    public String addShop(@RequestParam("userId") String userId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            if (Long.parseLong(userId) != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            UserDetailsResponseDto userDetailsResponseDto = userService.showDetailsInfo(Long.parseLong(userId));
            ShopRequestDto shopRequestDto = new ShopRequestDto();
            model.addAttribute("user", userDetailsResponseDto);
            model.addAttribute("shop", shopRequestDto);
            return "vendor/shop/add";
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/shops?userId=" + userId;
    }

    @PostMapping("/vendor/shops/add")
    public String addShop(@RequestParam("userId") String userId, @ModelAttribute("shop") @Valid ShopRequestDto shopRequestDto,
                          BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                UserDetailsResponseDto userDetailsResponseDto = userService.showDetailsInfo(Long.parseLong(userId));
                model.addAttribute("user", userDetailsResponseDto);
                return "vendor/shop/add";
            }
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            if (Long.parseLong(userId) != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            shopRequestDto.setUserId(Long.parseLong(userId));
            shopService.addShop(shopRequestDto);
            return "redirect:/vendor/shops?userId=" + userId;
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/shops?userId=" + userId;
    }

    @GetMapping("/vendor/shops/{shopId}/edit")
    public String editShop(@PathVariable("shopId") String shopId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            ShopRequestDto shopRequestDto = shopService.getEditShop(Long.parseLong(shopId));
            ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
            if (shopRequestDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            model.addAttribute("shop", shopRequestDto);
            model.addAttribute("info", shopResponseDto);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "vendor/shop/edit";
    }

    @PostMapping("/vendor/shops/{shopId}/edit")
    public String editShop(@PathVariable("shopId") String shopId, @ModelAttribute("shop") @Valid
    ShopRequestDto shopRequestDto, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
                model.addAttribute("info", shopResponseDto);
                return "vendor/shop/edit";
            }
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            if (shopRequestDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            shopService.editShop(Long.parseLong(shopId), shopRequestDto);
            return "redirect:/vendor/shops?userId=" + shopRequestDto.getUserId();
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "vendor/shop/edit";
    }

    @PostMapping("/vendor/shops/{shopId}/remove")
    public String removeShop(@PathVariable("shopId") String shopId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long currentUserId = userPrincipal.getId();
        try {
            ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
            if (shopResponseDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            shopService.removeShop(Long.parseLong(shopId));
            return "redirect:/vendor/shops?userId=" + shopResponseDto.getUserId();
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/shops?userId=" + currentUserId;
    }
}
