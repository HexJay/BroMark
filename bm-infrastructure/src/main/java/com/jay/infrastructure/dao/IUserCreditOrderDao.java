package com.jay.infrastructure.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.jay.infrastructure.dao.po.UserCreditOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jay
 * @date 2025/8/7 15:25
 * @description 用户积分流水单 DAO
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserCreditOrderDao {

    void insert(UserCreditOrder userCreditOrderReq);

}

