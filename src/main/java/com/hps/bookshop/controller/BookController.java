package com.hps.bookshop.controller;

import com.hps.bookshop.dto.BookResponseDto;
import com.hps.bookshop.dto.ShopResponseDto;
import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.service.book.BookService;
import com.hps.bookshop.service.shop.ShopService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@AllArgsConstructor
public class BookController {
    private final BookService bookService;
    private final ShopService shopService;

    @GetMapping({"/books", "/vendor/books"})
    public String getAllBooks(
            @RequestParam(value = "shopId", required = false) Long shopId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "6") Integer size,
            @RequestParam(value = "sort", defaultValue = "id,asc") String[] sort,
            Model model) {
        try {
            String sortField = sort[0];
            String sortDirection = sort[1];

            Direction direction = sortDirection.equals("desc") ? Direction.DESC : Direction.ASC;
            Order order = new Order(direction, sortField);

            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(order));
            Page<BookResponseDto> pageBooks;
            if (shopId == null) {
                if (keyword == null || keyword.isEmpty()) {
                    pageBooks = bookService.getAllBooks(pageable);
                } else {
                    pageBooks = bookService.getBooksWithSearching(null, keyword, pageable);
                }
            } else {
                if (keyword == null || keyword.isEmpty()) {
                    pageBooks = bookService.getBooksFromShop(shopId, pageable);
                } else {
                    pageBooks = bookService.getBooksWithSearching(shopId, keyword, pageable);
                }
            }
            List<BookResponseDto> books = pageBooks.getContent();
            model.addAttribute("shopId", shopId);
            model.addAttribute("books", books);
            model.addAttribute("currentPage", pageBooks.getNumber() + 1);
            model.addAttribute("totalItems", pageBooks.getTotalElements());
            model.addAttribute("totalPages", pageBooks.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDirection", sortDirection);
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "book/list";
    }

    @GetMapping({"/books/{bookId}", "/vendor/books/{bookId}"})
    public String getBook(@PathVariable("bookId") String bookId, Model model) {
        try {
            BookResponseDto bookResponseDto = bookService.getBook(Long.parseLong(bookId));
            ShopResponseDto shopResponseDto = shopService.getShop(bookResponseDto.getShopId());
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            if (shopResponseDto.getUserId().equals(userPrincipal.getId())) {
                model.addAttribute("isAuthorize", true);
            }

            model.addAttribute("book", bookResponseDto);
            model.addAttribute("shop", shopResponseDto);
            System.out.println(bookResponseDto.getTypes());
            System.out.println(bookService.getTypesFromBook(Long.parseLong(bookId)));
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "book/view";
    }
}
