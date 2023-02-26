package com.hps.bookshop.service.mail;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class MailContentBuilder {
    private final TemplateEngine templateEngine;
    public String buildVerificationToken(String link) {
        Context context = new Context();
        context.setVariable("link",link);
        return templateEngine.process("services/mail/activate-template",context);
    }
    public String buildVerificationCode(String code) {
        Context context = new Context();
        context.setVariable("code",code);
        return templateEngine.process("services/mail/code-template",context);
    }
}
