package com.hps.bookshop.repository;

import com.hps.bookshop.model.Shop;
import com.hps.bookshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {
    List<Shop> findByUser(User user);
}
