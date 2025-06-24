package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jay
 * @date 2025/6/24 21:05
 * @description 抽奖策略DAO
 */
@Mapper
public interface IStrategyDao {
    List<Strategy> queryStrategyList();
}
