package com.hps.bookshop.dto;

import com.hps.bookshop.validation.FieldsMatch;
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
public class ChangePasswordDto {
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Size(max = 30, message = "Password must contain up to 30 characters")
    private String password;
    private String confirmPassword;
    @NotNull(message = "Session required")
    private String sessionId;
}
