package com.hps.bookshop.service.shop;

import com.hps.bookshop.dto.ShopRequestDto;
import com.hps.bookshop.dto.ShopResponseDto;

import java.util.List;

public interface ShopService {
    List<ShopResponseDto> getAllShops();
    List<ShopResponseDto> getAllShops(Long userId);

    ShopResponseDto getShop(Long id);

    void addShop(ShopRequestDto shopRequestDto);

    ShopRequestDto getEditShop(long id);

    void editShop(Long id, ShopRequestDto shopRequestDto);

    void removeShop(long id);
}
