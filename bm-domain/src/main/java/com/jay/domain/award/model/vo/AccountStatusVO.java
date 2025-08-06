package com.jay.domain.award.model.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Jay
 * @date 2025/8/6 20:40
 * @description 账户状态枚举
 */
@Getter
@AllArgsConstructor
public enum AccountStatusVO {

    open("open", "开启"),
    close("close", "冻结"),
    ;

    private final String code;
    private final String desc;

}

