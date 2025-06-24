package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.StrategyAward;

import java.util.List;

/**
 * @author Jay
 * @date 2025/6/24 21:06
 * @description 抽奖策略明细DAO
 */
public interface IStrategyAwardDao {
    List<StrategyAward> queryStrategyAwardList();
}
