package com.jay.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/13 20:42
 * @description 抽奖活动账户表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleActivityAccount {
    /** 自增ID */
    private Long id;
    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 总次数 */
    private Integer totalCount;
    /** 总剩余次数 */
    private Integer totalCountSurplus;
    /** 日次数 */
    private Integer dayCount;
    /** 剩余日次数 */
    private Integer dayCountSurplus;
    /** 月次数 */
    private Integer monthCount;
    /** 剩余月次数 */
    private Integer monthCountSurplus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
