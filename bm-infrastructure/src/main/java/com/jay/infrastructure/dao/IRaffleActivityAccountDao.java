package com.jay.infrastructure.dao;

import com.jay.infrastructure.dao.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/13 20:59
 * @description 抽奖活动次数配置表DAO
 */
@Mapper
public interface IRaffleActivityAccountDao {
    void insert(RaffleActivityAccount raffleActivityAccount);

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);
}
