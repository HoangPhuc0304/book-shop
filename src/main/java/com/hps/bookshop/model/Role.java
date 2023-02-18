package com.hps.bookshop.model;

import com.hps.bookshop.entity.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private RoleType name;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH
//            , CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
//    @JoinTable(
//            name = "user_role",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> users;

    public Role(RoleType name) {
        this.name = name;
    }

//    public void addUser(User user) {
//        if (this.users == null)
//            this.users = new HashSet<>();
//        this.users.add(user);
//    }
//
//    public boolean removeRole(User user) {
//        return this.users.remove(user);
//    }
}
