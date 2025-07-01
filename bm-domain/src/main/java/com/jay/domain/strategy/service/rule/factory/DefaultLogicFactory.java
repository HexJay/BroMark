package com.jay.domain.strategy.service.rule.factory;

import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.service.annotation.LogicStrategy;
import com.jay.domain.strategy.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jay
 * @date 2025/6/30 17:13
 * @description
 */
@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    // Spring 会自动将所有实现了 ILogicFilter<?> 接口的 Bean 注入到构造函数参数 logicFilters 列表中
    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters) {
        logicFilters.forEach(logic -> {
            // 对每个注入的 Bean，通过 Spring 的 AnnotationUtils 查找其类上的 @LogicStrategy 注解
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                // 以注解中定义的 LogicModel.code（如 "rule_blacklist"）为 Key，将 Bean 实例存储在 Map 中
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回"),
        ;

        private final String code;
        private final String info;

    }
}
