package com.hps.bookshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class ChangePassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String code;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(nullable = false)
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(nullable = false)
    private Date expiryDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public ChangePassword(String code, Date createdDate, Date expiryDate, User user) {
        this.code = code;
        this.createdDate = createdDate;
        this.expiryDate = expiryDate;
        this.user = user;
    }
}
