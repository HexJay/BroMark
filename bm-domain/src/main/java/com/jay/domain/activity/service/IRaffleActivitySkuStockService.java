package com.jay.domain.activity.service;


import com.jay.domain.activity.model.vo.ActivitySkuStockKeyVO;

/**
 * @author Jay
 * @date 2025/7/20 23:31
 * @description 活动sku库存处理接口
 */
public interface IRaffleActivitySkuStockService {

    /**
     * 获取活动sku库存消耗队列
     * @return 奖品库存key信息
     * @throws InterruptedException 异常
     */
    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 清空队列
     */
    void clearQueue();

    /**
     * 延迟队列 + 任务趋势更新活动sku库存
     * @param Sku 活动商品
     */
    void updateActivitySkuStock(Long Sku);

    /**
     * 缓存库存消耗完毕，清空数据库库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
}
