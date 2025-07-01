package com.jay.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jay
 * @date 2025/6/30 16:58
 * @description 规则过滤校验类型值对象
 */
@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {

    ALLOW("0000", "放行，执行后续流程，不受规则引擎影响"),
    TAKE_OVER("0001", "接管：后续流程受规则引擎执行结果影响"),
    ;
    private final String code;
    private final String info;
}
