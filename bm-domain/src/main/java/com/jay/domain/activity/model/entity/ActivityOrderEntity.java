package com.jay.domain.activity.model.entity;


import com.jay.domain.activity.model.vo.OrderStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/16 16:24
 * @description 活动参与实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityOrderEntity {
    /**
     * 用户ID
     */
    private String userId;
    /** 商品sku */
    private Long sku;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 下单时间
     */
    private Date orderTime;
    /**
     * 总次数
     */
    private Integer totalCount;
    /**
     * 日次数
     */
    private Integer dayCount;
    /**
     * 月次数
     */
    private Integer monthCount;
    /**
     * 支付金额【积分】
     */
    private BigDecimal payAmount;
    /**
     * 订单状态
     */
    private OrderStateVO state;
    /** 业务仿重ID - 外部透传的，确保幂等*/
    private String outBusinessNo;
}
