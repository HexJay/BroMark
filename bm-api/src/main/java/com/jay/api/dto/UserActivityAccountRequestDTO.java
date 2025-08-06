package com.jay.api.dto;


import lombok.Data;

/**
 * @author Jay
 * @date 2025/8/6 15:05
 * @description 用户活动账户请求对象
 */
@Data
public class UserActivityAccountRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

}
