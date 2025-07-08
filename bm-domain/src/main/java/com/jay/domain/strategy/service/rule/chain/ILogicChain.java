package com.jay.domain.strategy.service.rule.chain;

import com.jay.domain.strategy.service.rule.chain.factory.DefaultChainFactory;

/**
 * @author Jay
 * @date 2025/7/4 14:22
 * @description 责任链接口
 */
public interface ILogicChain extends ILogicChainArmory{

    /**
     * 责任链接口
     * @param userId 用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId);

}
