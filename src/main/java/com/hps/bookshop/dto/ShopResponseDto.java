package com.hps.bookshop.dto;

import com.hps.bookshop.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponseDto {
    private Long id;
    private String name;
    private String address;
    private String imgUrl;
    private String description;
    private Double stars;
    private Date createdDate;
    private Integer bookAmounts;
    private Long userId;
}
