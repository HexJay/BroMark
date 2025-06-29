package com.jay.domain.strategy.service.armory;

/**
 * @author Jay
 * @date 2025/6/29 14:50
 * @description
 */
public interface IStrategyDispatch {

    /**
     * 获取抽奖策略装配的随机结果
     * @param strategyId
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);

    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);
}
