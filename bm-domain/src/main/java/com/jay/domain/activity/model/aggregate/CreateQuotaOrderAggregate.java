package com.jay.domain.activity.model.aggregate;


import com.jay.domain.activity.model.entity.ActivityAccountDayEntity;
import com.jay.domain.activity.model.entity.ActivityAccountEntity;
import com.jay.domain.activity.model.entity.ActivityAccountMonthEntity;
import com.jay.domain.activity.model.entity.UserRaffleOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/22 22:10
 * @description 参与活动订单聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateQuotaOrderAggregate {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 活动ID
     */
    private Long activityId;

    private ActivityAccountEntity activityAccountEntity;

    private boolean isExistAccountMonth = true;

    private ActivityAccountMonthEntity activityAccountMonthEntity;

    private boolean isExistAccountDay = true;

    private ActivityAccountDayEntity activityAccountDayEntity;

    private UserRaffleOrderEntity userRaffleOrderEntity;
}
