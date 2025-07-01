package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jay
 * @date 2025/6/24 21:06
 * @description 抽奖策略奖品明细配置DAO
 */
@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);
}
