package com.jay.domain.strategy.repository;

import com.jay.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jay
 * @date 2025/6/27 19:56
 * @description 策略仓储接口
 */
public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTables(Long strategyId, Integer rateRange, HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTables);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int rateKey);
}
