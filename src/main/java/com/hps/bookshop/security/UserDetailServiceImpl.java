package com.hps.bookshop.security;

import com.hps.bookshop.entity.UserPrincipal;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.model.User;
import com.hps.bookshop.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws NotFoundException {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find user with email: " + email));
        return UserPrincipal.build(user, null);
    }

    public UserDetails loadUserById(Long id) throws NotFoundException {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find user with id: " + id));
        return UserPrincipal.build(user, null);
    }
}
