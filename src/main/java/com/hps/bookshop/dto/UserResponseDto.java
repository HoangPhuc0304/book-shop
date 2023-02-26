package com.hps.bookshop.dto;

import com.hps.bookshop.entity.StatusAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class UserResponseDto {
    protected String name;
    protected String email;
    protected String imgUrl;
    protected Date dob;
    protected String address;
    protected String phone;
    protected Integer shopAmounts = 0;
}
