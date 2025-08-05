package com.jay.domain.rebate.service;

import com.jay.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * @author Jay
 * @date 2025/8/5 14:53
 * @description 行为返利服务接口
 */
public interface IBehaviorRebateService {

    /**
     * 创建行为动作的入账订单
     *
     * @param behaviorEntity 行为实体对象
     * @return 订单ID
     */
    List<String> createOrder(BehaviorEntity behaviorEntity);

}
