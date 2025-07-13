package com.jay.infrastructure.dao.po;

import lombok.Data;

/**
 * @author Jay
 * @date 2025/7/13 20:45
 * @description 抽奖活动订单表
 */
@Data
public class RaffleActivityOrder {
    /**
     * 自增ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 抽奖策略ID
     */
    private String strategyId;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 下单时间
     */
    private String orderTime;
    /**
     * 订单状态（not_used、used、expire）
     */
    private String state;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;
}
