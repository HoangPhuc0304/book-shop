package com.hps.bookshop.dto;

import com.hps.bookshop.entity.StatusAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Date createdDate;
    private StatusAccount status;
    private Date bob;
    private String address;
    private String phone;
    private String roleName;
    private String shopName;
}
