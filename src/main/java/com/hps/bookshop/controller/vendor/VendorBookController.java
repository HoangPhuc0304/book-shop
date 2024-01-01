package com.hps.bookshop.controller.vendor;

import com.hps.bookshop.dto.*;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotMatchException;
import com.hps.bookshop.model.BookType;
import com.hps.bookshop.service.book.BookService;
import com.hps.bookshop.service.shop.ShopService;
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
public class VendorBookController {

    private final BookService bookService;
    private final ShopService shopService;

    @GetMapping("/vendor/books/add")
    public String addBook(@RequestParam("shopId") String shopId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
            if (shopResponseDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            BookRequestDto bookRequestDto = new BookRequestDto();
            List<BookType> bookTypes = bookService.getAllBookTypes();
            model.addAttribute("book", bookRequestDto);
            model.addAttribute("shop", shopResponseDto);
            model.addAttribute("bookTypes", bookTypes);
            return "vendor/book/add";
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/books?shopId=" + shopId;
    }

    @PostMapping("/vendor/books/add")
    public String addBook(@RequestParam("shopId") String shopId, @ModelAttribute("book") @Valid BookRequestDto bookRequestDto,
                          BindingResult bindingResult, Model model) {
        try {
            System.out.println(bookRequestDto);
            shopId = shopId.replaceAll(",", "");
            System.out.println(shopId);
            if (bindingResult.hasErrors()) {
                ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
                List<BookType> bookTypes = bookService.getAllBookTypes();
                model.addAttribute("shop", shopResponseDto);
                model.addAttribute("bookTypes", bookTypes);
                return "vendor/book/add";
            }
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            ShopResponseDto shopResponseDto = shopService.getShop(Long.parseLong(shopId));
            if (shopResponseDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            bookRequestDto.setShopId(Long.parseLong(shopId));
            bookService.addBook(bookRequestDto);
            return "redirect:/vendor/shops/" + shopId;
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/shops/" + shopId;
    }

    @GetMapping("/vendor/books/{bookId}/edit")
    public String editBook(@PathVariable("bookId") String bookId, Model model) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            BookRequestDto bookRequestDto = bookService.getEditBook(Long.parseLong(bookId));
            BookResponseDto bookResponseDto = bookService.getBook(Long.parseLong(bookId));
            ShopResponseDto shopResponseDto = shopService.getShop(bookResponseDto.getShopId());
            List<BookType> bookTypes = bookService.getAllBookTypes();
            if (shopResponseDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            System.out.println("Book: " + bookRequestDto);
            model.addAttribute("book", bookRequestDto);
            model.addAttribute("info", bookResponseDto);
            model.addAttribute("bookTypes", bookTypes);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/shops";
    }

    @PostMapping("/vendor/books/{bookId}/edit")
    public String editBook(@PathVariable("bookId") String bookId, @ModelAttribute("book") @Valid
    BookRequestDto bookRequestDto, BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                BookResponseDto bookResponseDto = bookService.getBook(Long.parseLong(bookId));
                List<BookType> bookTypes = bookService.getAllBookTypes();
                model.addAttribute("info", bookResponseDto);
                model.addAttribute("bookTypes", bookTypes);
                return "vendor/shop/edit";
            }
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long currentUserId = userPrincipal.getId();
            ShopResponseDto shopResponseDto = shopService.getShop(bookRequestDto.getShopId());
            if (shopResponseDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            bookService.editBook(Long.parseLong(bookId), bookRequestDto);
            return "redirect:/vendor/shops/" + bookRequestDto.getShopId();
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "vendor/shop/edit";
    }

    @PostMapping("/vendor/books/{bookId}/remove")
    public String removeBook(@PathVariable("bookId") String bookId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long currentUserId = userPrincipal.getId();
        BookResponseDto bookResponseDto = bookService.getBook(Long.parseLong(bookId));
        try {
            ShopResponseDto shopResponseDto = shopService.getShop(bookResponseDto.getShopId());
            if (shopResponseDto.getUserId() != currentUserId) {
                throw new NotMatchException("Not authorize");
            }
            bookService.removeBook(Long.parseLong(bookId));
            return "redirect:/vendor/shops/" + bookResponseDto.getShopId();
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "redirect:/vendor/shops/" + bookResponseDto.getShopId();
    }
}
