package com.jay.domain.strategy.service.rule.impl;

import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.model.entity.RuleMatterEntity;
import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.annotation.LogicStrategy;
import com.jay.domain.strategy.service.rule.ILogicFilter;
import com.jay.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/7/2 14:07
 * @description 抽奖n次，解锁某些奖品
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleDuring> {

    @Resource
    private IStrategyRepository repository;

    private Long userRaffleCount = 0L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleDuring> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤 - 奖品解锁 userId:{}, strategyId:{}, ruleModel:{}",
                ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        long raffleUnlockCount = Long.parseLong(ruleValue);

        // 达到解锁条件
        if (userRaffleCount >= raffleUnlockCount) {
            return RuleActionEntity.<RuleActionEntity.RaffleDuring>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();

        }

        return RuleActionEntity.<RuleActionEntity.RaffleDuring>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }

}
