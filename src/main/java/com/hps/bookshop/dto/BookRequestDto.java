package com.hps.bookshop.dto;

import com.hps.bookshop.model.BookType;
import com.hps.bookshop.model.Shop;
import com.hps.bookshop.validation.ValidImage;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String author;
    @ValidImage
    private MultipartFile image;
    @Size(max = 500, message = "Description must contain up to 500 characters")
    private String description;
    @DecimalMin(value = "0.0")
    private Double price;
    @NotNull
    private Set<BookType> types;
    private Long shopId;
}
