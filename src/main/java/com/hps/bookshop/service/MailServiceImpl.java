package com.hps.bookshop.service;

import com.hps.bookshop.entity.MailDetails;
import com.hps.bookshop.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final MailContentBuilder mailContentBuilder;
    @Value("${spring.mail.username}")
    private String mailSender;

    @Override
    @Async
    public void sendSimpleEmail(MailDetails emailDetails) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(mailSender);
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());
            helper.setText(mailContentBuilder.build(emailDetails.getLink()),true);
        };

        try {
            javaMailSender.send(preparator);
            log.info("Sent activate mail to email: " + emailDetails.getRecipient());
        } catch (Exception exc) {
            log.error(exc.getMessage());
            throw new ExternalServiceException("There are some error when sending mail to email: "
                    + emailDetails.getRecipient());
        }
    }
}
