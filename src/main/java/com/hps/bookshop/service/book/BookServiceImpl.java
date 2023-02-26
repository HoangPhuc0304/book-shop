package com.hps.bookshop.service.book;

import com.hps.bookshop.dto.BookRequestDto;
import com.hps.bookshop.dto.BookResponseDto;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.mapper.BookMapper;
import com.hps.bookshop.model.Book;
import com.hps.bookshop.model.BookType;
import com.hps.bookshop.model.Shop;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.BookRepository;
import com.hps.bookshop.repository.BookTypeRepository;
import com.hps.bookshop.repository.ShopRepository;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.service.file.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private final ShopRepository shopRepository;
    private final BookRepository bookRepository;
    private final BookTypeRepository bookTypeRepository;
    private final FileStorageService fileStorageService;
    private final BookMapper bookMapper;

    @Override
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(bookMapper::mapBookToResponseDto);
    }

    @Override
    public List<BookResponseDto> getBooksFromType(Long typeId) {
        List<Book> books = bookRepository.findByTypesId(typeId);
        return books.stream()
                .map(bookMapper::mapBookToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Set<BookType> getTypesFromBook(Long bookId) {
        Set<BookType> types = bookTypeRepository.findByBooksId(bookId);
        return types;
    }

    @Override
    public List<BookResponseDto> getBooksFromShop(Long shopId) {
        List<Book> books = bookRepository.findByShopId(shopId);
        return books.stream()
                .map(bookMapper::mapBookToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookResponseDto> getBooksFromShop(Long shopId, Pageable pageable) {
        Page<Book> books = bookRepository.findByShopId(shopId, pageable);
        return books.map(bookMapper::mapBookToResponseDto);
    }

    @Override
    public BookResponseDto getBook(Long id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find book with id: " + id));
        return bookMapper.mapBookToResponseDto(book);
    }

    @Override
    @Transactional
    public void addBook(BookRequestDto bookRequestDto) {
        Book book = bookMapper.mapRequestDtoToBook(bookRequestDto);
        if (bookRequestDto.getImage().getSize() > 0) {
            String fileName = fileStorageService.save(bookRequestDto.getImage());
            book.setImageName(fileName);
        }
        bookRepository.save(book);
        Shop shop = book.getShop();
        shop.addBook(book);
        shopRepository.save(shop);
    }

    @Override
    public BookRequestDto getEditBook(long id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find book with id: " + id));
        return bookMapper.mapBookToRequestDto(book);
    }

    @Override
    @Transactional
    public void editBook(Long id, BookRequestDto bookRequestDto) {
        System.out.println(bookRequestDto);
        String fileName = null;
        String fileOldName = null;
        try {
            //Update Shop
            Book book = bookRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException(
                            "Cannot find book with id: " + id));
            //Update User table
            if (bookRequestDto.getName() != null && !bookRequestDto.getName().isEmpty()) {
                book.setName(bookRequestDto.getName());
            }
            if (bookRequestDto.getAuthor() != null && !bookRequestDto.getAuthor().isEmpty()) {
                book.setAuthor(bookRequestDto.getAuthor());
            }
            if (bookRequestDto.getDescription() != null && !bookRequestDto.getDescription().isEmpty()) {
                book.setDescription(bookRequestDto.getDescription());
            }
            if (bookRequestDto.getPrice() != null) {
                book.setPrice(bookRequestDto.getPrice());
            }
            if (bookRequestDto.getTypes() != null) {
                book.setTypes(bookRequestDto.getTypes());
            }

            //Check & save image file if exists
            if (bookRequestDto.getImage().getSize() > 0
                    && bookRequestDto.getImage().getOriginalFilename() != null
                    && !bookRequestDto.getImage().getOriginalFilename().equals(book.getImageName())) {
                fileOldName = book.getImageName();
                fileName = fileStorageService.save(bookRequestDto.getImage());
            }
            if (fileName != null) {
                book.setImageName(fileName);
            }

            bookRepository.save(book);
            if (fileOldName != null) {
                fileStorageService.delete(fileOldName);
            }
        } catch (Exception exc) {
            //Delete file when error occur
            if (fileName != null) {
                fileStorageService.delete(fileName);
            }
            throw new RuntimeException(exc);
        }

    }

    @Override
    @Transactional
    public void removeBook(long id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find book with id: " + id));
        String fileName = book.getImageName();
        Shop shop = book.getShop();
        shop.removeBook(book);
        bookRepository.deleteById(id);
        fileStorageService.delete(fileName);
        shopRepository.save(shop);
    }

    @Override
    public List<BookType> getAllBookTypes() {
        return bookTypeRepository.findAll();
    }

    @Override
    public Page<BookResponseDto> getBooksWithSearching(Long shopId, String keyword, Pageable pageable) {
        Page<Book> bookPage;
        if (shopId == null) {
            bookPage = bookRepository.findByKeyword(keyword, pageable);
        } else {
            bookPage = bookRepository.findByShopIdAndKeyword(shopId, keyword, pageable);
        }
        return bookPage.map(bookMapper::mapBookToResponseDto);
    }
}
