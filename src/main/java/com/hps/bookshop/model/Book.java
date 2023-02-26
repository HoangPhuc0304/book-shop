package com.hps.bookshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String author;
    @NotNull
    private String imageName;
    @Lob
    @Column(length = 1000)
    private String description;
    @DecimalMin(value = "0.0")
    private Double price;
    private Integer voteUp = 0;
    private Integer voteDown = 0;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createdDate;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH
            , CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(
            name = "book_book_type",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "book_type_id")
    )
    private Set<BookType> types;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH
            , CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public Book(Book book) {
        this.id = book.id;
        this.name = book.name;
        this.author = book.author;
        this.imageName = book.imageName;
        this.description = book.description;
        this.price = book.price;
        this.voteUp = book.voteUp;
        this.voteDown = book.voteDown;
        this.createdDate = book.createdDate;
        this.types = book.types;
        this.shop = book.shop;
    }
}
