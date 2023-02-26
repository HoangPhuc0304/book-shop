package com.hps.bookshop.service.user;

import com.hps.bookshop.dto.UserDetailsResponseDto;
import com.hps.bookshop.dto.UserEditDto;
import com.hps.bookshop.dto.UserResponseDto;

public interface UserService {
    UserResponseDto showInfo(Long id);
    UserDetailsResponseDto showDetailsInfo(Long id);
    UserEditDto showEditUser(Long id);
    void updateEditUser(Long id, UserEditDto userEditDto);
}
