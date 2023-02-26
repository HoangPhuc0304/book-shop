package com.hps.bookshop.dto;

import com.hps.bookshop.model.User;
import com.hps.bookshop.validation.ValidImage;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequestDto {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 100, message = "Address must contain up to 100 characters")
    private String address;
    @ValidImage
    private MultipartFile image;
    @Size(max = 500, message = "Description must contain up to 500 characters")
    private String description;
    private Long userId;
}
