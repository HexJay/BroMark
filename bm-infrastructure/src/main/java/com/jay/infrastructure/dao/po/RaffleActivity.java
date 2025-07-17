package com.jay.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/13 20:29
 * @description 抽奖活动表
 */
@Data
public class RaffleActivity {

    /** 自增ID */
    private Long id;
    /** 活动ID */
    private Long activityId;
    /** 活动名称 */
    private String activityName;
    /** 活动描述 */
    private String activityDesc;
    /** 开始时间 */
    private Date beginDateTime;
    /** 结束时间 */
    private Date endDateTime;
    /** 抽奖策略ID */
    private Long strategyId;
    /** 活动状态 */
    private String state;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
