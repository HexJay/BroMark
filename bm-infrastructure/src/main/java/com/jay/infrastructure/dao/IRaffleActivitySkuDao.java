package com.jay.infrastructure.dao;


import com.jay.infrastructure.dao.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/16 15:49
 * @description 商品sku dao
 */
@Mapper
public interface IRaffleActivitySkuDao {
    RaffleActivitySku queryRaffleActivitySku(Long sku);

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    List<RaffleActivitySku> queryActivitySkuListByActivityId(Long activityId);
}
