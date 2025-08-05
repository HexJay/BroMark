package com.jay.infrastructure.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.jay.infrastructure.dao.po.UserBehaviorRebateOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/8/5 13:59
 * @description 用户行为返利流水订单表
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserBehaviorRebateOrderDao {

    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);

}
