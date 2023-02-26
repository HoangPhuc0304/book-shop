package com.hps.bookshop.repository;

import com.hps.bookshop.model.Book;
import com.hps.bookshop.model.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTypesId(Long typeId);

    List<Book> findByShopId(Long shopId);

    Page<Book> findByShopId(Long shopId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(b.name) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    Page<Book> findByKeyword(String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.shop.id = :shopId AND " +
            "(LOWER(b.author) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(b.name) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<Book> findByShopIdAndKeyword(Long shopId, String keyword, Pageable pageable);
}
