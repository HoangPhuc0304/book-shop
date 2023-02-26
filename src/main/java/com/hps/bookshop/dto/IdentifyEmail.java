package com.hps.bookshop.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifyEmail {
    @Email
    private String email;
}
