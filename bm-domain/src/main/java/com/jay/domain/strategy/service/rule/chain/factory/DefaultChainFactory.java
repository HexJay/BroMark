package com.jay.domain.strategy.service.rule.chain.factory;

import com.jay.domain.strategy.model.entity.StrategyEntity;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Jay
 * @date 2025/7/4 15:42
 * @description 责任链工厂
 */
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> chains;

    private final IStrategyRepository repository;

    // 通过构造函数注入所有实现了的责任链节点 Map<String, ILogicChain> chains, String 存放的是对象的 bean 名称。
    public DefaultChainFactory(Map<String, ILogicChain> chains, IStrategyRepository repository) {
        this.chains = chains;
        this.repository = repository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();

        if (ruleModels == null || ruleModels.length == 0) {
            return chains.get("default");
        }

        // 构建责任链
        ILogicChain logicChain = chains.get(ruleModels[0]);
        ILogicChain cur = logicChain;

        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = chains.get(ruleModels[i]);
            cur = cur.appendNext(nextChain);
        }

        cur.appendNext(chains.get("default"));
        return logicChain;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /**
         * 奖品ID
         */
        private Integer awardId;
        /**
         * 奖品规则
         */
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WEIGHT("rule_weight", "权重抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_DEFAULT("rule_default", "默认抽奖");

        private final String code;
        private final String info;
    }
}
