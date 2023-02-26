package com.hps.bookshop.model;

import com.hps.bookshop.entity.AuthProvider;
import com.hps.bookshop.entity.StatusAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude="shops")
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email(message = "This must be email format")
    private String email;
    private String password;
    private String name;
    private String imgUrl;
    private String imgName;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createdDate;
    private StatusAccount status;
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_detail_id")
    private UserDetail userDetail;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH
            , CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Shop> shops;

    public User(String password, String email, Date createdDate, StatusAccount status) {
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
    public void addShop(Shop shop) {
        if(this.shops == null)
            this.shops = new HashSet<>();
        this.shops.add(shop);
    }
    public boolean removeShop(Shop shop) {
        return this.shops.remove(shop);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", createdDate=" + createdDate +
                ", status=" + status +
                ", provider=" + provider +
                ", providerId='" + providerId + '\'' +
                ", userDetail=" + userDetail +
                ", roles=" + roles +
                '}';
    }
}
