package com.jay.domain.activity.service.quota.policy;


import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

/**
 * @author Jay
 * @date 2025/8/7 23:38
 * @description 交易策略接口，包括；返利兑换（不用支付），积分订单（需要支付）
 */
public interface ITradePolicy {

    void trade(CreateOrderAggregate createOrderAggregate);
}
