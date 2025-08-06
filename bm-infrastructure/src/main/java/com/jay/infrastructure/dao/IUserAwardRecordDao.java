package com.jay.infrastructure.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.jay.infrastructure.dao.po.UserAwardRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/7/22 21:04
 * @description 用户中奖记录表
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserAwardRecordDao {
    void insert(UserAwardRecord userAwardRecord);

    int updateAwardRecordCompletedState(UserAwardRecord userAwardRecordReq);
}
