package com.jay.domain.strategy.service.rule.chain;

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
    Integer logic(String userId, Long strategyId);

}
