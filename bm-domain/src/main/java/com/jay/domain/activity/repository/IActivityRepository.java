package com.jay.domain.activity.repository;


import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.entity.ActivityCountEntity;
import com.jay.domain.activity.model.entity.ActivityEntity;
import com.jay.domain.activity.model.entity.ActivitySkuEntity;
import com.jay.domain.activity.model.vo.ActivitySkuStockKeyVO;

import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/16 16:33
 * @description 活动仓储接口
 */
public interface IActivityRepository {
    ActivitySkuEntity queryActivitySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

    void doSaveOrder(CreateOrderAggregate aggregate);

    void cacheActivitySkuStockCount(String key, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String key, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);
}
