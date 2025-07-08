package com.jay.domain.strategy.service.rule.tree.factory.engine;

import com.jay.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * @author Jay
 * @date 2025/7/5 15:35
 * @description 规则树组合接口
 */
public interface IDecisionTreeEngine {

    DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId);
}
