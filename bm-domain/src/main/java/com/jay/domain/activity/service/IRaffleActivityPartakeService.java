package com.jay.domain.activity.service;


import com.jay.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.jay.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * @author Jay
 * @date 2025/7/22 21:46
 * @description 抽奖活动参与服务
 */
public interface IRaffleActivityPartakeService {
    /**
     * 创建抽奖单：用户参与抽奖活动，扣减活动账户库存，产生抽奖单。如存在未被使用的抽奖单则直接返回已存在的抽奖单。
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);
}
