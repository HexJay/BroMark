package com.jay.domain.strategy.model.entity;

import com.jay.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jay
 * @date 2025/6/29 16:30
 * @description 策略规则实体
 */
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyRuleEntity {
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖奖品ID【规则类型为策略，则不需要奖品ID】
     */
    private Integer awardId;
    /**
     * 抽象规则类型；1-策略规则、2-奖品规则
     */
    private Integer ruleType;
    /**
     * 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】
     */
    private String ruleModel;
    /**
     * 抽奖规则比值
     */
    private String ruleValue;
    /**
     * 抽奖规则描述
     */
    private String ruleDesc;

    /**
     * 获取权重值
     * @示例： 4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     */
    public Map<String, List<Integer>> getRuleWeightValues() {
        if (!"rule_weight".equals(ruleModel)) return null;
        // 1.分割不同积分的规则（空格）
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<String, List<Integer>> resMap = new HashMap<>();

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
            String[] valueStrings = kv[1].split(Constants.SPLIT);
            List<Integer> values = new ArrayList<>();
            for (String value : valueStrings) {
                values.add(Integer.parseInt(value));
            }
            resMap.put(kv[0], values);
        }
        log.info("抽奖 - 积分与范围：{}", resMap);
        return resMap;
    }
}
