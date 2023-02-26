package com.hps.bookshop.service.shop;

import com.hps.bookshop.dto.ShopRequestDto;
import com.hps.bookshop.dto.ShopResponseDto;
import com.hps.bookshop.exception.NotFoundException;
import com.hps.bookshop.mapper.ShopMapper;
import com.hps.bookshop.model.Shop;
import com.hps.bookshop.model.User;
import com.hps.bookshop.model.UserDetail;
import com.hps.bookshop.repository.ShopRepository;
import com.hps.bookshop.repository.UserRepository;
import com.hps.bookshop.service.file.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ShopMapper shopMapper;

    @Override
    public List<ShopResponseDto> getAllShops() {
        List<Shop> shops = shopRepository.findAll();
        return shops.stream()
                .map(shopMapper::mapShopToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShopResponseDto> getAllShops(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find user with id: " + userId));
        List<Shop> shops = shopRepository.findByUser(user);
        System.out.println(shops);
        return shops.stream()
                .map(shopMapper::mapShopToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ShopResponseDto getShop(Long id) {
        Shop shop = shopRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find shop with id: " + id));
        return shopMapper.mapShopToResponseDto(shop);
    }

    @Override
    @Transactional
    public void addShop(ShopRequestDto shopRequestDto) {
        Shop shop = shopMapper.mapRequestDtoToShop(shopRequestDto);
        if (shopRequestDto.getImage().getSize() > 0) {
            String fileName = fileStorageService.save(shopRequestDto.getImage());
            shop.setImageName(fileName);
        }
        shopRepository.save(shop);
        User user = shop.getUser();
        user.addShop(shop);
        userRepository.save(user);
    }

    @Override
    public ShopRequestDto getEditShop(long id) {
        Shop shop = shopRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find shop with id: " + id));
        return shopMapper.mapShopToRequestDto(shop);
    }

    @Override
    @Transactional
    public void editShop(Long id, ShopRequestDto shopRequestDto) {
        System.out.println(shopRequestDto);
        String fileName = null;
        String fileOldName = null;
        try {
            //Update Shop
            Shop shop = shopRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException(
                            "Cannot find shop with id: " + id));
            //Update User table
            if (shopRequestDto.getName() != null && !shopRequestDto.getName().isEmpty()) {
                shop.setName(shopRequestDto.getName());
            }
            if (shopRequestDto.getAddress() != null && !shopRequestDto.getAddress().isEmpty()) {
                shop.setAddress(shopRequestDto.getAddress());
            }
            if (shopRequestDto.getDescription() != null && !shopRequestDto.getDescription().isEmpty()) {
                shop.setDescription(shopRequestDto.getDescription());
            }

            //Check & save image file if exists
            if (shopRequestDto.getImage().getSize() > 0
                    && shopRequestDto.getImage().getOriginalFilename() != null
                    && !shopRequestDto.getImage().getOriginalFilename().equals(shop.getImageName())) {
                fileOldName = shop.getImageName();
                fileName = fileStorageService.save(shopRequestDto.getImage());
            }
            if (fileName != null) {
                shop.setImageName(fileName);
            }

            shopRepository.save(shop);
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

    @Override
    @Transactional
    public void removeShop(long id) {
        Shop shop = shopRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Cannot find shop with id: " + id));
        String fileName = shop.getImageName();
        User user = shop.getUser();
        user.removeShop(shop);
        shopRepository.deleteById(id);
        fileStorageService.delete(fileName);
        userRepository.save(user);
    }
}
