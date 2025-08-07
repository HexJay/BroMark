package com.jay.domain.credit.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Jay
 * @date 2025/8/7 15:34
 * @description TODO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAccountEntity {

    /** 用户ID */
    private String userId;
    /** 调整值 */
    private BigDecimal adjustAmount;
}
