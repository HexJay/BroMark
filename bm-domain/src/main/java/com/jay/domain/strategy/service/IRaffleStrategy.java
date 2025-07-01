package com.jay.domain.strategy.service;

import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @author Jay
 * @date 2025/6/30 16:33
 * @description 抽奖策略接口
 */
public interface IRaffleStrategy {

    /**
     * 执行抽奖
     * @param entity 抽奖所需信息
     * @return 抽奖结果
     */
    RaffleAwardEntity performRaffle(RaffleFactorEntity entity);
}
