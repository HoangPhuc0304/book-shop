package com.hps.bookshop.dto;

import com.hps.bookshop.validation.PhoneConstraint;
import com.hps.bookshop.validation.ValidImage;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class UserEditDto {
    private Long id;
    private String name;
    @ValidImage
    private MultipartFile image;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    @Size(max = 100, message = "Address must contain up to 100 characters")
    private String address;
    @PhoneConstraint(message = "Phone must contain 9-11 digits")
    private String phone;
}
