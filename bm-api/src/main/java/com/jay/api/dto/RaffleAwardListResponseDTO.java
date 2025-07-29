package com.jay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/9 22:46
 * @description 奖品列表返回结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardListResponseDTO {

    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Integer awardId;
    /**
     * 抽奖奖品标题
     */
    private String awardTitle;
    /**
     * 抽奖奖品副标题
     */
    private String awardSubtitle;
    /**
     * 排序
     */
    private Integer sort;
    /** 奖品次数规则 */
    private Integer awardLockCount;
    /**
     * 奖品是否解锁
     */
    private Boolean isAwardUnlock;
    /**
     * 等待解锁次数
     */
    private Integer waitUnlockCount;
}
