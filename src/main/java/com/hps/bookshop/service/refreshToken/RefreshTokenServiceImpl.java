package com.hps.bookshop.service.refreshToken;

import com.hps.bookshop.model.RefreshToken;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${refreshToken.expiration.time}")
    private int refreshTokenExpirationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public RefreshToken generateRefreshToken(User user) {
        //Delete old refresh token if exist
        refreshTokenRepository.deleteByUser(user);

        //Create new refresh token
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, refreshTokenExpirationMs);
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                calendar.getTime(),
                user
        );

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        return refreshToken.isPresent() && (refreshToken.get().getExpiryDate().getTime() > new Date().getTime());
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> findByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }
}
