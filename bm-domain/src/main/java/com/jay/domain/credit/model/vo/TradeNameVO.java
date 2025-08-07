package com.jay.domain.credit.model.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jay
 * @date 2025/8/7 15:36
 * @description 交易名称枚举值
 */
@Getter
@AllArgsConstructor
public enum TradeNameVO {

    REBATE("行为返利"),
    CONVERT_SKU("兑换抽奖"),
    ;

    private final String name;

}

