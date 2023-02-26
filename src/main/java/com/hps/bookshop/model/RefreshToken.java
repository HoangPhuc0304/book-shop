package com.hps.bookshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(nullable = false)
    private Date expiryDate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken(String token, Date expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }
}
