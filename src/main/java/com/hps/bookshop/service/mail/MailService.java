package com.hps.bookshop.service.mail;

import com.hps.bookshop.entity.MailDetails;

public interface MailService {
    void sendSimpleEmail(MailDetails emailDetails);
}
