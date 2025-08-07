package com.jay.domain.credit.repository;


import com.jay.domain.credit.model.aggregate.TradeAggregate;

/**
 * @author Jay
 * @date 2025/8/7 15:29
 * @description 用户积分仓储
 */
public interface ICreditRepository {

    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);
}

