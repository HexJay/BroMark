package com.jay.domain.activity.service.armory;


import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/19 16:07
 * @description 活动调度【扣减库存】
 */
public interface IActivityDispatch {

    /**
     *
     * @param sku
     * @param endDateTime 活动过期时间
     * @return
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);
}
