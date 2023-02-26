package com.hps.bookshop.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifyCode {
    @Email
    private String email;
    private String code;
}
