package com.jay.api.dto;


import lombok.Data;

/**
 * @author Jay
 * @date 2025/8/8 14:58
 * @description 商品购物车请求对象
 */
@Data
public class SkuProductShopCartRequestDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * sku 商品
     */
    private Long sku;

}
