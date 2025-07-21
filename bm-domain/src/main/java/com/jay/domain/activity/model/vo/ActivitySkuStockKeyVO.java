package com.jay.domain.activity.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/19 16:40
 * @description 活动sku库存 key值对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVO {
    private Long sku;
    private Long activityId;
}
