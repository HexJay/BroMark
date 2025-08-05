package com.jay.infrastructure.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.jay.infrastructure.dao.po.RaffleActivityAccountMonth;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/22 21:02
 * @description 抽奖活动账户表-月次数
 */
@Mapper
public interface IRaffleActivityAccountMonthDao {

    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    void insertActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    void addAccountQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
