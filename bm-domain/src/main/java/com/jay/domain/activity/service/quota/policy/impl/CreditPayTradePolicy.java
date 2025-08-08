package com.jay.domain.activity.service.quota.policy.impl;


import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.vo.OrderStateVO;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

/**
 * @author Jay
 * @date 2025/8/7 23:40
 * @description 积分兑换，支付类订单
 */
@Service("credit_pay_trade")
public class CreditPayTradePolicy implements ITradePolicy {

    private final IActivityRepository activityRepository;

    public CreditPayTradePolicy(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void trade(CreateOrderAggregate createOrderAggregate) {
        createOrderAggregate.setOrderState(OrderStateVO.WAIT_PAY);
        activityRepository.doSaveCreditPayOrder(createOrderAggregate);
    }
}
