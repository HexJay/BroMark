package com.jay.domain.strategy.service.armory.algorithm;


import com.jay.domain.strategy.model.entity.StrategyAwardEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Jay
 * @date 2025/8/16 23:40
 * @description TODO
 */
public interface IAlgorithm {

    void armoryAlgorithm(String key, List<StrategyAwardEntity> strategyAwardEntities, BigDecimal rateRange);

    Integer dispatchAlgorithm(String key);
}
