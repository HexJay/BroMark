package com.jay.domain.strategy.service.rule.tree;

import com.jay.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * @author Jay
 * @date 2025/7/5 15:22
 * @description 规则树接口
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue);

}
