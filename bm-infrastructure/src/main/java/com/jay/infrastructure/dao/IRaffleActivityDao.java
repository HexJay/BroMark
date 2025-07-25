package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/13 20:59
 * @description 抽奖活动表DAO
 */
@Mapper
public interface IRaffleActivityDao {

    RaffleActivity queryRaffleActivityByActivityId(Long activityId);
}
