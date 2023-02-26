package com.hps.bookshop.repository;

import com.hps.bookshop.model.User;
import com.hps.bookshop.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long> {
    void deleteByUser(User user);

    Optional<VerificationCode> findByUser(User user);
}
