package com.jay.domain.strategy.service.rule.tree.impl;

import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.jay.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.jay.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Jay
 * @date 2025/7/5 15:25
 * @description 幸运奖（兜底）
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {
        log.info("规则过滤 - 兜底奖品 userId:{}, strategyId:{}, awardId:{}, ruleValue:{}", userId, strategyId, awardId, ruleValue);
        String[] split = ruleValue.split(Constants.COLON);
        if (split.length == 0) {
            log.error("规则过滤 - 兜底奖品，奖品未配置警告 userId:{}, strategyId:{}, awardId:{}", userId, strategyId, awardId);
            throw new RuntimeException("兜底奖品未配置 " + ruleValue);
        }

        // 兜底奖品配置
        Integer luckyAwardId = Integer.valueOf(split[0]);
        String awardRuleValue = split.length > 1 ? split[1] : "";

        // 返回兜底奖品
        log.info("规则过滤 - 兜底奖品 userId:{}, strategyId:{}, luckyAwardId:{}, awardRuleValue:{}", userId, strategyId, awardId, ruleValue);
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(luckyAwardId)
                        .awardRuleValue(awardRuleValue)
                        .build())
                .build();
    }
}
