package com.jay.infrastructure.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.jay.infrastructure.dao.po.RaffleActivityAccountDay;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/22 21:01
 * @description 抽奖活动账户表-日次数
 */
@Mapper
public interface IRaffleActivityAccountDayDao {

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDayReq);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay raffleActivityAccountDayReq);

    void insertActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDayReq);
}
