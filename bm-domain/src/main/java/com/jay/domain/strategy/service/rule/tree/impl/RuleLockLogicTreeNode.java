package com.jay.domain.strategy.service.rule.tree.impl;

import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.jay.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/7/5 15:24
 * @description 解锁判别节点
 */
@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyRepository repository;

    private Long userCount = 10L;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        log.info("规则树 - 解锁判别 userId:{}, strategyId:{}, awardId:{}", userId, strategyId, awardId);

        String ruleValue = repository.queryStrategyRuleValue(strategyId, awardId,
                DefaultTreeFactory.LogicModel.RULE_LOCK.getCode());

        if (ruleValue == null || ruleValue.isEmpty()) {
            throw new RuntimeException("awardId未配置规则");
        }

        Long requireCount = Long.valueOf(ruleValue);

        if (requireCount > userCount) {
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .build();
        }

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .build())
                .build();
    }
}
