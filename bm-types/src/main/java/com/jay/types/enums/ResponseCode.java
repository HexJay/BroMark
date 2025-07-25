package com.jay.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS("200", "成功"),
    FAILURE("-1", "失败"),
    UN_ERROR("0001", "未知失败"),
    ILLEGAL_PARAMETER("0002", "非法参数"),
    INDEX_DUP("0003","唯一索引冲突"),
    STRATEGY_RULE_WEIGHT_IS_NULL("ERR_BIZ_001","业务异常，策略规则中weight_rule权重已使用但未配置"),
    UN_ASSEMBLED_STRATEGY_ARMORY("ERR_BIZ_002","抽奖策略未装配，通过IStrategyArmory装配"),
    ACTIVITY_STATE_ERROR("ERR_BIZ_003", "活动未开启（非open状态）"),
    ACTIVITY_DATE_ERROR("ERR_BIZ_004", "非活动日期范围"),
    ACTIVITY_SKU_STOCK_ERROR("ERR_BIZ_005", "活动库存不足"),
    ;

    private String code;
    private String info;

}
