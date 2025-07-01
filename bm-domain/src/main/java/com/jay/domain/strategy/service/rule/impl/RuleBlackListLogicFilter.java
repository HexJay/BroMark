package com.jay.domain.strategy.service.rule.impl;

import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.model.entity.RuleMatterEntity;
import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.annotation.LogicStrategy;
import com.jay.domain.strategy.service.rule.ILogicFilter;
import com.jay.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.jay.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/6/30 19:59
 * @description
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBlackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBefore> {

    @Resource
    private IStrategyRepository repository;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBefore> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤 - 黑名单 userId:{}, strategyId:{}, ruleModel:{}",
                ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();
        // 获取当前策略的规则（获取黑名单？）
        // 数据形式为：100:user001,user002,user003
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        // 拆分
        String[] splitRuleValues = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValues[0]);

        String[] userBlackIds = splitRuleValues[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            // 黑名单过滤
            if (userId.equals(userBlackId)) {
                return RuleActionEntity.<RuleActionEntity.RaffleBefore>builder()
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                        .data(RuleActionEntity.RaffleBefore.builder()
                                .strategyId(ruleMatterEntity.getStrategyId())
                                .awardId(awardId) // 设定黑名单只能抽到的奖品
                                .build())
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .build();
            }
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBefore>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }
}
