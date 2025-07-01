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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jay
 * @date 2025/6/30 20:55
 * @description
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBefore> {

    @Resource
    private IStrategyRepository repository;

    private Long userScore = 4500L;

    /**
     * 权重规则过滤 <p>
     * 1.权重规则格式：4000:102,103 5000:102,103,104 <p>
     * 2.解析数据格式：判断那个范围符合用户
     *
     * @param ruleMatterEntity
     * @return
     */
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBefore> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤 - 权重范围 userId:{}, strategyId:{}, ruleModel:{}",
                ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();

        String ruleValue = repository.queryStrategyRuleValue(
                ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getAwardId(),
                ruleMatterEntity.getRuleModel()
        );

        // 1.解析权重策略
        Map<Long, String> valueMap = parseRuleWeight(ruleValue);
        // 2.判断权重策略是否存在
        if (valueMap == null || valueMap.isEmpty()) {
            // 2.1.不存在，放行
            return RuleActionEntity.<RuleActionEntity.RaffleBefore>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 2.1.2.权重规则存在，转换key值，默认排序
        ArrayList<Long> keys = new ArrayList<>(valueMap.keySet());
        Collections.sort(keys);

        // 3. 找出最小符合规则
        Long fittedKey = keys.stream()
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        if (fittedKey != null) {
            return RuleActionEntity.<RuleActionEntity.RaffleBefore>builder()
                    .data(RuleActionEntity.RaffleBefore.builder()
                            .strategyId(strategyId)
                            .ruleWeightKey(String.valueOf(fittedKey))
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();

        }

        return RuleActionEntity.<RuleActionEntity.RaffleBefore>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    private Map<Long, String> parseRuleWeight(String ruleValue) {
        // 1.分割不同积分的规则（空格）
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> resMap = new HashMap<>();

        for (String group : ruleValueGroups) {
            // 检查输入
            if (StringUtils.isBlank(group)) {
                return resMap;
            }
            // 2.分割key和value
            String[] kv = group.split(Constants.COLON);
            if (kv.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format：" + group);
            }
            // 3.解析值
            resMap.put(Long.parseLong(kv[0]), group);
        }
        return resMap;
    }
}
