package com.hps.bookshop.service.password;

import com.hps.bookshop.dto.ChangePasswordDto;
import com.hps.bookshop.model.ChangePassword;

public interface ChangePasswordService {
    ChangePassword generateSessionByEmail(String email);

    void updateNewPassword(String sessionId, ChangePasswordDto changePasswordDto);
}
