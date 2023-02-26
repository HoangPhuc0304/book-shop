package com.hps.bookshop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MailDetails {
    private String recipient;
    private String subject;
    private String text;
    private Boolean isHtml;
}
