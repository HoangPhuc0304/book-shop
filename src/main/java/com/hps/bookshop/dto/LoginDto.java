package com.hps.bookshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 8, message = "Username must contain at least 8 characters")
    @Size(max = 30, message = "Username must contain up to 30 characters")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Size(max = 30, message = "Password must contain up to 30 characters")
    private String password;
}
