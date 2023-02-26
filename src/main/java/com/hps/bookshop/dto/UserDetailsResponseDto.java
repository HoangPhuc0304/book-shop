package com.hps.bookshop.dto;

import com.hps.bookshop.entity.StatusAccount;
import com.hps.bookshop.model.Role;

import java.util.Date;
import java.util.Set;

public class UserDetailsResponseDto extends UserResponseDto{
    private Long id;
    private Date createdDate;
    private StatusAccount status;
    private Set<Role> roles;
    private Integer purchasedBookAmounts = 0;

    public UserDetailsResponseDto(String name, String email, String imgUrl, Date dob, String address, String phone, Integer shopAmounts, Long id, Date createdDate, StatusAccount status, Set<Role> roles, Integer purchasedBookAmounts) {
        super(name, email, imgUrl, dob, address, phone, shopAmounts);
        this.id = id;
        this.createdDate = createdDate;
        this.status = status;
        this.roles = roles;
        this.purchasedBookAmounts = purchasedBookAmounts;
    }

    public Long getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public StatusAccount getStatus() {
        return status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Integer getPurchasedBookAmounts() {
        return purchasedBookAmounts;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setStatus(StatusAccount status) {
        this.status = status;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setPurchasedBookAmounts(Integer purchasedBookAmounts) {
        this.purchasedBookAmounts = purchasedBookAmounts;
    }

    @Override
    public String toString() {
        return "UserDetailsResponseDto{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", status=" + status +
                ", roles=" + roles +
                ", purchasedBookAmounts=" + purchasedBookAmounts +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", dob=" + dob +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", shopAmounts=" + shopAmounts +
                '}';
    }
}
