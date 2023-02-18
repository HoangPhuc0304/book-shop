package com.hps.bookshop.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class MailContentBuilder {
    private final TemplateEngine templateEngine;
    public String build(String link) {
        Context context = new Context();
        context.setVariable("link",link);
        return templateEngine.process("mail/mail-template",context);
    }
}
