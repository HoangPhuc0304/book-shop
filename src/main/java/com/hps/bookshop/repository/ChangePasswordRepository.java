package com.hps.bookshop.repository;

import com.hps.bookshop.model.ChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChangePasswordRepository extends JpaRepository<ChangePassword,Long> {
    Optional<ChangePassword> findByCode(String code);
}
