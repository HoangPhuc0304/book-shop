package com.hps.bookshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude="books")
@AllArgsConstructor
@NoArgsConstructor
public class BookType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH
            , CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(
            name = "book_book_type",
            joinColumns = @JoinColumn(name = "book_type_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books;

    public void addBook(Book book) {
        if(this.books == null)
            this.books = new HashSet<>();
        this.books.add(book);
    }
    public boolean removeBook(Book book) {
        return this.books.remove(book);
    }

    @Override
    public String toString() {
        return "BookType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
