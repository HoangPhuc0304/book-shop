package com.hps.bookshop.entity;

import com.hps.bookshop.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails, OAuth2User {
    private final Long id;
    private final String email;
    private final String password;
    private final Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String email, String password, Boolean enabled,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserPrincipal build(User user, Map<String, Object> attributes) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        UserPrincipal userPrincipal = new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(),
                user.getStatus() == StatusAccount.ACTIVATED, authorities);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getEmail() {
        return this.email;
    }

    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        UserPrincipal user = (UserPrincipal) o;
        return Objects.equals(this.id, user.id);
    }
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    @Override
    public String getName() {
        return String.valueOf(this.id);
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                ", attributes=" + attributes +
                '}';
    }
}
