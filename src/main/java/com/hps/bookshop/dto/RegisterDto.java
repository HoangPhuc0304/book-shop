package com.hps.bookshop.dto;

import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.validation.FieldsMatch;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldsMatch(first = "password", second = "confirmPassword", message = "Password and Confirm password don't match")
public class RegisterDto {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 8, message = "Username must contain at least 8 characters")
    @Size(max = 30, message = "Username must contain up to 30 characters")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Size(max = 30, message = "Password must contain up to 30 characters")
    private String password;
    private String confirmPassword;
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private RoleType role;
    @Enumerated(value = EnumType.STRING)
    private RoleType[] roleTypes = RoleType.values();
    @Email
    private String email;

}
