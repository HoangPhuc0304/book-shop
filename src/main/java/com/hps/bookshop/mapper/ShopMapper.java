package com.hps.bookshop.mapper;

import com.hps.bookshop.dto.ShopRequestDto;
import com.hps.bookshop.dto.ShopResponseDto;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.Shop;
import com.hps.bookshop.model.User;
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

@Mapper(componentModel = "spring")
@Slf4j
public abstract class ShopMapper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Mapping(target = "id", source = "shop.id")
    @Mapping(target = "imgUrl", expression = "java(getImgUrl(shop))")
    @Mapping(target = "bookAmounts", expression = "java(getBookAmounts(shop))")
    @Mapping(target = "userId", expression = "java(getUserId(shop))")
    abstract public ShopResponseDto mapShopToResponseDto(Shop shop);

    @Mapping(target = "id", source = "shop.id")
    @Mapping(target = "image", expression = "java(getImageFile(shop))")
    @Mapping(target = "userId", expression = "java(getUserId(shop))")
    abstract public ShopRequestDto mapShopToRequestDto(Shop shop);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    @Mapping(target = "createdDate", expression = "java(getNewDate())")
    @Mapping(target = "user", expression = "java(getUser(shopRequestDto))")
    abstract public Shop mapRequestDtoToShop(ShopRequestDto shopRequestDto);

    String getImgUrl(Shop shop) {
        if (shop.getImageName() == null) {
            return null;
        }
        return fileStorageService.getUrl(shop.getImageName());
    }
    Integer getBookAmounts(Shop shop) {
        return shop.getBooks().size();
    }
    Long getUserId(Shop shop) {
        return shop.getUser().getId();
    }
    MultipartFile getImageFile(Shop shop) {
        String fileName = shop.getImageName();
        if(fileName != null) {
            try {
                Resource file = fileStorageService.load(fileName);
                Path path = file.getFile().toPath();
                String mimeType = Files.probeContentType(path);
                return new MockMultipartFile(fileName,fileName,mimeType,file.getInputStream());
            } catch (Exception exc) {
                log.error(exc.getMessage());
            }
        }
        return null;
    }
    Date getNewDate() {
        return new Date();
    }
    User getUser(ShopRequestDto shopRequestDto) {
        return userRepository
                .findById(shopRequestDto.getUserId())
                .orElseThrow(()-> new NotFoundException("Cannot find user with id: "
                        + shopRequestDto.getUserId()));
    }
}
