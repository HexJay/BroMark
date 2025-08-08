package com.jay.domain.activity.model.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jay
 * @date 2025/8/7 23:35
 * @description 订单交易类型
 */
@Getter
@AllArgsConstructor
public enum OrderTradeTypeVO {

    CREDIT_PAY_TRADE("credit_pay_trade", "积分兑换，需要支付类交易"),
    REBATE_NO_PAY_TRADE("rebate_no_pay_trade", "返利奖品，不需要支付类交易"),
    ;
    private final String code;
    private final String desc;
}
