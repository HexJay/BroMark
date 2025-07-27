package com.jay.domain.activity.service.armory;


/**
 * @author Jay
 * @date 2025/7/19 15:54
 * @description 活动装配接口
 */
public interface IActivityArmory {

    boolean assembleActivitySkuByActivity(Long activityId);

    boolean assembleActivitySku(Long sku);
}
