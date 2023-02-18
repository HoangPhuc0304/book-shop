package com.hps.bookshop.service;

import com.hps.bookshop.model.RefreshToken;
import com.hps.bookshop.model.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken generateRefreshToken(User user);
    boolean validateRefreshToken(String token);
    Optional<RefreshToken> findByToken(String token);
}
