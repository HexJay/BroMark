package com.jay.domain.strategy.service.armory;

/**
 * @author Jay
 * @date 2025/6/27 19:40
 * @description 策略装配工厂，负责初始化策略计算
 */
public interface IStrategyArmory {

    void assembleLotteryStrategy(Long strategyId);

    Integer getRandomAwardId(Long strategyId);
}
