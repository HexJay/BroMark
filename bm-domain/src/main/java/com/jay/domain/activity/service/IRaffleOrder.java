package com.jay.domain.activity.service;


import com.jay.domain.activity.model.entity.ActivityOrderEntity;
import com.jay.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * @author Jay
 * @date 2025/7/16 16:29
 * @description TODO
 */
public interface IRaffleOrder {
    /**
     * 以sku创建抽奖活动订单，获得参与抽奖资格（可消耗的次数）
     *
     * @param activityShopCartEntity 活动sku实体，通过sku领取活动。
     * @return 活动参与记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);
}
