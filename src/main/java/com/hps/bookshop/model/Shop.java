package com.hps.bookshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude="books")
@NoArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String imageName;
    @Lob
    @Column(length = 1000)
    private String description;
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private Double stars;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createdDate;
    @OneToMany(mappedBy = "shop",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Book> books;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH
            , CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    public Shop(String name, String description, Double stars, Date createdDate) {
        this.name = name;
        this.description = description;
        this.stars = stars;
        this.createdDate = createdDate;
    }

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
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", imageName='" + imageName + '\'' +
                ", description='" + description + '\'' +
                ", stars=" + stars +
                ", createdDate=" + createdDate +
                ", user=" + user +
                '}';
    }
}
