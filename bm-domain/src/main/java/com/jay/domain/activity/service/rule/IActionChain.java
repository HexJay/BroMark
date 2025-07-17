package com.jay.domain.activity.service.rule;


import com.jay.domain.activity.model.entity.ActivityCountEntity;
import com.jay.domain.activity.model.entity.ActivityEntity;
import com.jay.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @author Jay
 * @date 2025/7/17 15:04
 * @description 抽奖活动责任链规则接口
 */
public interface IActionChain extends IActionChainArmory{

    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
