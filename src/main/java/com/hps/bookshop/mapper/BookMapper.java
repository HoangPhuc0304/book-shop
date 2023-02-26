package com.hps.bookshop.mapper;

import com.hps.bookshop.dto.BookRequestDto;
import com.hps.bookshop.dto.BookResponseDto;
import com.hps.bookshop.dto.ShopRequestDto;
import com.hps.bookshop.dto.ShopResponseDto;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.Book;
import com.hps.bookshop.model.BookType;
import com.hps.bookshop.model.Shop;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.BookTypeRepository;
import com.hps.bookshop.repository.ShopRepository;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.service.file.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Set;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class BookMapper {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private BookTypeRepository bookTypeRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @Mapping(target = "imgUrl", expression = "java(getImgUrl(book))")
    @Mapping(target = "shopId", expression = "java(getShopId(book))")
    @Mapping(target = "types", expression = "java(getBookTypes(book))")
    abstract public BookResponseDto mapBookToResponseDto(Book book);

    @Mapping(target = "image", expression = "java(getImageFile(book))")
    @Mapping(target = "shopId", expression = "java(getShopId(book))")
    @Mapping(target = "types", expression = "java(getBookTypes(book))")
    abstract public BookRequestDto mapBookToRequestDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    @Mapping(target = "createdDate", expression = "java(getNewDate())")
    @Mapping(target = "shop", expression = "java(getShop(bookRequestDto))")
    abstract public Book mapRequestDtoToBook(BookRequestDto bookRequestDto);

    String getImgUrl(Book book) {
        if (book.getImageName() == null) {
            return null;
        }
        return fileStorageService.getUrl(book.getImageName());
    }

    Long getShopId(Book book) {
        return book.getShop().getId();
    }

    Set<BookType> getBookTypes(Book book) {
        Set<BookType> bookTypes = bookTypeRepository.findByBooksId(book.getId());
        return bookTypes;
    }

    MultipartFile getImageFile(Book book) {
        String fileName = book.getImageName();
        if (fileName != null) {
            try {
                Resource file = fileStorageService.load(fileName);
                Path path = file.getFile().toPath();
                String mimeType = Files.probeContentType(path);
                return new MockMultipartFile(fileName, fileName, mimeType, file.getInputStream());
            } catch (Exception exc) {
                log.error(exc.getMessage());
            }
        }
        return null;
    }

    Date getNewDate() {
        return new Date();
    }

    Shop getShop(BookRequestDto bookRequestDto) {
        return shopRepository
                .findById(bookRequestDto.getShopId())
                .orElseThrow(() -> new NotFoundException("Cannot find shop with id: "
                        + bookRequestDto.getShopId()));
    }
}
