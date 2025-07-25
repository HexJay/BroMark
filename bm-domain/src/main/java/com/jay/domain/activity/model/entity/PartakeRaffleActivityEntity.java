package com.jay.domain.activity.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/22 21:48
 * @description 参与活动抽奖实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartakeRaffleActivityEntity {
    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
}
