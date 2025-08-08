package com.jay.domain.activity.service.quota.policy.impl;


import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.jay.domain.activity.model.vo.OrderStateVO;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Jay
 * @date 2025/8/7 23:39
 * @description 返利无支付交易订单，直接充值到账
 */
@Service("rebate_no_pay_trade")
public class RebateNoPayTradePolicy implements ITradePolicy {

    private final IActivityRepository activityRepository;

    public RebateNoPayTradePolicy(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }


    @Override
    public void trade(CreateOrderAggregate createOrderAggregate) {
        // 不需要支付则修改订单金额为0，状态为完成，直接给用户账户充值
        createOrderAggregate.setOrderState(OrderStateVO.COMPLETED);
        createOrderAggregate.getActivityOrderEntity().setPayAmount(BigDecimal.ZERO);
        activityRepository.doSaveNoPayOrder(createOrderAggregate);
    }
}
