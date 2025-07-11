package com.jay.domain.strategy.service;

import com.jay.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/10 16:36
 * @description 策略奖品查询接口
 */
public interface IRaffleAward {

    /**
     * 根据策略Id查询抽奖奖品列表配置
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwards(Long strategyId);
}
