package com.jay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/9 23:05
 * @description 抽奖返回结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleStrategyResponseDTO {

    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Integer awardId;
    /** 排序编号 【策略奖品顺序编号】 */
    private Integer awardIndex;
}
