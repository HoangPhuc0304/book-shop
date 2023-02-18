package com.hps.bookshop.model;

import com.hps.bookshop.validation.PhoneConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date bob;
    @Size(max = 100, message = "Address must contain up to 100 characters")
    private String address;
    @PhoneConstraint(message = "Phone must contain 9-11 digits")
    private String phone;
    @OneToOne(mappedBy = "userDetail",fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH,CascadeType.MERGE,
                    CascadeType.PERSIST,CascadeType.REFRESH})
    private User user;
}

