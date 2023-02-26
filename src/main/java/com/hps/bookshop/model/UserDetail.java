package com.hps.bookshop.model;

import com.hps.bookshop.validation.PhoneConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(exclude="user")
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    @Size(max = 100, message = "Address must contain up to 100 characters")
    private String address;
    @PhoneConstraint(message = "Phone must contain 9-11 digits")
    private String phone;
    @OneToOne(mappedBy = "userDetail",fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH,CascadeType.MERGE,
                    CascadeType.PERSIST,CascadeType.REFRESH})
    private User user;

    @Override
    public String toString() {
        return "UserDetail{" +
                "id=" + id +
                ", dob=" + dob +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

