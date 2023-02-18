package com.hps.bookshop.model;

import com.hps.bookshop.entity.StatusAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email"),
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 8, message = "Username must contain at least 8 characters")
    @Size(max = 30, message = "Username must contain up to 30 characters")
    private String username;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @Email
    private String email;
    private Date createdDate;
    private StatusAccount status;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_detail_id")
    private UserDetail userDetail;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH
            , CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH
//            , CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
//    private Set<Shop> shops;

    public User(String username, String password, String email, Date createdDate, StatusAccount status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdDate = createdDate;
        this.status = status;
    }

    public void addRole(Role role) {
        if(this.roles == null)
            this.roles = new HashSet<>();
        this.roles.add(role);
    }
    public boolean removeRole(Role role) {
        return this.roles.remove(role);
    }
}
