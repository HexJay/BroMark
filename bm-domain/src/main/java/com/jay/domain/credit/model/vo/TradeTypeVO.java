package com.jay.domain.credit.model.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jay
 * @date 2025/8/7 15:34
 * @description TODO
 */
@Getter
@AllArgsConstructor
public enum TradeTypeVO {
    FORWARD("forward", "正向交易，+ 积分"),
    REVERSE("reverse", "逆向交易，- 积分"),
    ;

    private final String code;
    private final String info;
}
