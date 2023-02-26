package com.hps.bookshop.service.book;

import com.hps.bookshop.dto.BookRequestDto;
import com.hps.bookshop.dto.BookResponseDto;
import com.hps.bookshop.dto.ShopRequestDto;
import com.hps.bookshop.dto.ShopResponseDto;
import com.hps.bookshop.model.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface BookService {
    Page<BookResponseDto> getAllBooks(Pageable pageable);

    List<BookResponseDto> getBooksFromType(Long typeId);

    Set<BookType> getTypesFromBook(Long bookId);
    List<BookResponseDto> getBooksFromShop(Long shopId);

    Page<BookResponseDto> getBooksFromShop(Long shopId, Pageable pageable);

    BookResponseDto getBook(Long id);

    void addBook(BookRequestDto bookRequestDto);

    BookRequestDto getEditBook(long id);

    void editBook(Long id, BookRequestDto bookRequestDto);

    void removeBook(long id);

    List<BookType> getAllBookTypes();

    Page<BookResponseDto> getBooksWithSearching(Long shopId, String keyword, Pageable pageable);
}
