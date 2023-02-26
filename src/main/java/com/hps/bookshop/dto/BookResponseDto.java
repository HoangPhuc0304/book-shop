package com.hps.bookshop.dto;

import com.hps.bookshop.model.BookType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto {
    private Long id;
    private String name;
    private String author;
    private String imgUrl;
    private String description;
    private Double price;
    private Date createdDate;
    private Integer voteUp;
    private Integer voteDown;
    private Set<BookType> types;
    private Long shopId;
}
