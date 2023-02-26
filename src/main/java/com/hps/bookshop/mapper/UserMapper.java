package com.hps.bookshop.mapper;

import com.hps.bookshop.dto.UserDetailsResponseDto;
import com.hps.bookshop.dto.UserEditDto;
import com.hps.bookshop.dto.UserResponseDto;
import com.hps.bookshop.model.User;
import com.hps.bookshop.service.file.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class UserMapper {
    @Autowired
    private FileStorageService fileStorageService;

    @Mapping(target = "imgUrl", expression = "java(getImgUrl(user))")
    @Mapping(target = "dob", expression = "java(getDob(user))")
    @Mapping(target = "address", expression = "java(getAddress(user))")
    @Mapping(target = "phone", expression = "java(getPhone(user))")
    @Mapping(target = "shopAmounts", expression = "java(getShopAmounts(user))")
    public abstract UserResponseDto mapUserToResponseDto(User user);

    @Mapping(target = "imgUrl", expression = "java(getImgUrl(user))")
    @Mapping(target = "dob", expression = "java(getDob(user))")
    @Mapping(target = "address", expression = "java(getAddress(user))")
    @Mapping(target = "phone", expression = "java(getPhone(user))")
    @Mapping(target = "shopAmounts", expression = "java(getShopAmounts(user))")
    public abstract UserDetailsResponseDto mapUserToDetailResponseDto(User user);

    @Mapping(target = "dob", expression = "java(getDob(user))")
    @Mapping(target = "address", expression = "java(getAddress(user))")
    @Mapping(target = "phone", expression = "java(getPhone(user))")
    @Mapping(target = "image", expression = "java(getImageFile(user))")
    public abstract UserEditDto mapUserToEditDto(User user);

    String getImgUrl(User user) {
        if (user.getImgName() == null) {
            return user.getImgUrl();
        }
        return fileStorageService.getUrl(user.getImgName());
    }

    Date getDob(User user) {
        if (user.getUserDetail() == null)
            return null;
        return user.getUserDetail().getDob();
    }

    String getAddress(User user) {
        if (user.getUserDetail() == null)
            return null;
        return user.getUserDetail().getAddress();
    }

    String getPhone(User user) {
        if (user.getUserDetail() == null)
            return null;
        return user.getUserDetail().getPhone();
    }

    Integer getShopAmounts(User user) {
        return user.getShops().size();
    }

    MultipartFile getImageFile(User user) {
        String fileName = user.getImgName();
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
}
