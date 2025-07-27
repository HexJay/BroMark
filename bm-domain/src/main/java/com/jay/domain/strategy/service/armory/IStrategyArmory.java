package com.jay.domain.strategy.service.armory;

/**
 * @author Jay
 * @date 2025/6/27 19:40
 * @description 策略装配工厂，负责初始化策略计算
 */
public interface IStrategyArmory {

    /**
     * 装配抽奖策略配置 （触发的时机可以为活动审核通过后进行调用）
     *
     * @param strategyId
     * @return
     */
    Boolean assembleLotteryStrategy(Long strategyId);

    Boolean assembleLotteryStrategyByActivityId(Long activityId);
}
