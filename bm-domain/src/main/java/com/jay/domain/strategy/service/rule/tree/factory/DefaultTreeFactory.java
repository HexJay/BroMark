package com.jay.domain.strategy.service.rule.tree.factory;

import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.model.vo.tree.RuleTreeVO;
import com.jay.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.jay.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import com.jay.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Jay
 * @date 2025/7/5 15:32
 * @description 规则树工厂
 */
@Service
public class DefaultTreeFactory {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> treeMap) {
        this.logicTreeNodeMap = treeMap;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO){
        return new DecisionTreeEngine(logicTreeNodeMap, ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity {
        private RuleLogicCheckTypeVO ruleLogicCheckType;
        private StrategyAwardData strategyAwardData;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardData {
        /**
         * 奖品ID
         */
        private Integer awardId;
        /**
         * 奖品规则
         */
        private String awardRuleValue;
    }
}