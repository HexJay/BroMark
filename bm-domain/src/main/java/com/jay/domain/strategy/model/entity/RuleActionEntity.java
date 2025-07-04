package com.jay.domain.strategy.model.entity;

import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * @author Jay
 * @date 2025/6/30 16:47
 * @description 规则动作实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    // 明确过滤的是哪个规则
    private String ruleModel;
    // 动作包含的数据
    private T data;


    static public class RaffleEntity {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // 抽奖前动作
    static public class RaffleBefore extends RaffleEntity {
        /**
         * 策略ID
         */
        private Long strategyId;
        /**
         * 抽奖奖品ID - 内部流转使用
         **/
        private Integer awardId;
        /**
         * 权重值key，用于抽奖时可以选择权重抽奖
         */
        private String ruleWeightKey;
    }

    // 抽奖前动作
    static public class RaffleDuring extends RaffleEntity {

    }

    // 抽奖前动作
    static public class RaffleAfter extends RaffleEntity {

    }
}
