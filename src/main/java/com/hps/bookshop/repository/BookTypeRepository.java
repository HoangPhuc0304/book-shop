package com.hps.bookshop.repository;

import com.hps.bookshop.model.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookTypeRepository extends JpaRepository<BookType,Long> {
    Set<BookType> findByBooksId(Long bookId);
}
