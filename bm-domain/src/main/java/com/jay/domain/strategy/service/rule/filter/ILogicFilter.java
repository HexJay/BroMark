package com.jay.domain.strategy.service.rule.filter;

import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @author Jay
 * @date 2025/6/30 16:42
 * @description 抽奖规则过滤接口
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
