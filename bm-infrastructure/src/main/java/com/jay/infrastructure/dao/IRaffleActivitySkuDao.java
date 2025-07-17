package com.jay.infrastructure.dao;


import com.jay.infrastructure.dao.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/16 15:49
 * @description 商品sku dao
 */
@Mapper
public interface IRaffleActivitySkuDao {
    RaffleActivitySku queryRaffleActivitySku(Long sku);
}
