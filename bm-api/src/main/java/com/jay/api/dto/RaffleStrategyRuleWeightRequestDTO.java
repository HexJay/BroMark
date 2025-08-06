package com.jay.api.dto;


import lombok.Data;

/**
 * @author Jay
 * @date 2025/8/6 15:31
 * @description 抽奖策略规则，权重配置，查询N次抽奖可解锁奖品范围，请求对象
 */
@Data
public class RaffleStrategyRuleWeightRequestDTO {

    // 用户ID
    private String userId;
    // 抽奖活动ID
    private Long activityId;

}
