package com.jay.domain.strategy.service;


import java.util.Map;

/**
 * @author Jay
 * @date 2025/7/29 22:12
 * @description 抽奖规则接口
 */
public interface IRaffleRule {

    Map<String, Integer> queryAwardRuleLockCount(String... treeIds);
}
