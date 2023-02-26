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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        return "vendor/book/list";
    }

    @GetMapping({"/books/{bookId}", "/vendor/books/{bookId}"})
    public String getBook(@PathVariable("bookId") String bookId, Model model) {
        try {
            BookResponseDto bookResponseDto = bookService.getBook(Long.parseLong(bookId));
            ShopResponseDto shopResponseDto = shopService.getShop(bookResponseDto.getShopId());

            model.addAttribute("book", bookResponseDto);
            model.addAttribute("shop", shopResponseDto);
            System.out.println(bookResponseDto.getTypes());
            System.out.println(bookService.getTypesFromBook(Long.parseLong(bookId)));
        } catch (Exception exc) {
            log.error(exc.getMessage());
        }
        return "vendor/book/view";
    }

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
