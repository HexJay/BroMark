package com.jay.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/6/30 16:36
 * @description 奖品实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {

    /**
     * 抽奖奖品ID - 内部流转使用
     **/
    private Integer awardId;
    /**
     * 奖品配置信息
     **/
    private String awardConfig;
    /**
     * 排序
     */
    private Integer sort;
}
