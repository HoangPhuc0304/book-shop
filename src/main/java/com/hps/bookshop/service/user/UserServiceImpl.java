package com.hps.bookshop.service.user;

import com.hps.bookshop.dto.UserDetailsResponseDto;
import com.hps.bookshop.dto.UserEditDto;
import com.hps.bookshop.dto.UserResponseDto;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.mapper.UserMapper;
import com.hps.bookshop.model.User;
import com.hps.bookshop.model.UserDetail;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.service.file.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto showInfo(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find user with id: " + id));
        return userMapper.mapUserToResponseDto(user);
    }

    @Override
    public UserDetailsResponseDto showDetailsInfo(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find user with id: " + id));
        return userMapper.mapUserToDetailResponseDto(user);
    }

    @Override
    public UserEditDto showEditUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find user with id: " + id));
        return userMapper.mapUserToEditDto(user);
    }

    @Override
    @Transactional
    public void updateEditUser(Long id, UserEditDto userEditDto) {
        System.out.println(userEditDto);
        String fileName = null;
        String fileOldName = null;
        try {
            //Update User table
            User user = userRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException(
                            "Cannot find user with id: " + id));

            //Check & save image file if exists
            if (userEditDto.getImage().getSize() > 0
                    && userEditDto.getImage().getOriginalFilename() != null
                    && !userEditDto.getImage().getOriginalFilename().equals(user.getImgName())) {
                fileOldName = user.getImgName();
                fileName = fileStorageService.save(userEditDto.getImage());
            }
            //Update User table
            if (userEditDto.getName() != null && !userEditDto.getName().isEmpty()) {
                user.setName(userEditDto.getName());
            }
            if (fileName != null) {
                user.setImgName(fileName);
            }

            //Update User Detail table
            UserDetail userDetail;
            if (user.getUserDetail() != null) {
                userDetail = user.getUserDetail();
            } else {
                userDetail = new UserDetail();
            }

            if (userEditDto.getDob() != null) {

                userDetail.setDob(userEditDto.getDob());
            }
            if (userEditDto.getAddress() != null && !userEditDto.getAddress().isEmpty()) {
                userDetail.setAddress(userEditDto.getAddress());
            }
            if (userEditDto.getPhone() != null && !userEditDto.getPhone().isEmpty()) {
                userDetail.setPhone(userEditDto.getPhone());
            }

            user.setUserDetail(userDetail);
            userRepository.save(user);
            if (fileOldName != null) {
                fileStorageService.delete(fileOldName);
            }
        } catch (Exception exc) {
            //Delete file when error occur
            if (fileName != null) {
                fileStorageService.delete(fileName);
            }
            throw new RuntimeException(exc);
        }
    }
}
