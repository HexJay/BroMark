package com.jay.domain.activity.service.partake;


import com.jay.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.jay.domain.activity.model.entity.*;
import com.jay.domain.activity.model.vo.UserRaffleOrderStateVO;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.types.enums.ResponseCode;
import com.jay.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/22 22:16
 * @description TODO
 */
@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake {

    private final SimpleDateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM");
    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    protected RaffleActivityPartakeService(IActivityRepository repository) {
        super(repository);
    }

    @Override
    protected CreateQuotaOrderAggregate doFilterAccount(String userId, Long activityId, Date now) {
        // 查询用户额度
        ActivityAccountEntity activityAccountEntity = repository.queryActivityAccount(userId, activityId);
        // 额度判断（只判断总剩余额度）
        if (null == activityAccountEntity || activityAccountEntity.getTotalCountSurplus() <= 0) {
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        // 查询月账户额度
        String month = dateFormatMonth.format(now);
        ActivityAccountMonthEntity activityAccountMonthEntity = repository.queryActivityAccountMonth(userId, activityId, month);
        if (null != activityAccountMonthEntity && activityAccountMonthEntity.getMonthCountSurplus() <= 0) {
            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
        }

        // 创建月账户额度：true = 存在月账户、false = 不存在月账户
        boolean isExistAccountMonth = null != activityAccountMonthEntity;
        if (!isExistAccountMonth) {
            activityAccountMonthEntity = ActivityAccountMonthEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .activityId(activityId)
                    .month(month)
                    .monthCount(activityAccountEntity.getMonthCount())
                    .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                    .build();
        }

        // 查询日账户额度
        String day = dateFormatDay.format(now);
        ActivityAccountDayEntity activityAccountDayEntity = repository.queryActivityAccountDay(userId, activityId, day);
        if (null != activityAccountDayEntity && activityAccountDayEntity.getDayCountSurplus() <= 0) {
            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
        }

        // 创建日账户额度：true = 存在日账户、false = 不存在日账户
        boolean isExistAccountDay = null != activityAccountDayEntity;
        if (!isExistAccountDay) {
            activityAccountDayEntity = ActivityAccountDayEntity.builder()
                    .userId(userId)
                    .activityId(activityId)
                    .activityId(activityId)
                    .day(day)
                    .dayCount(activityAccountEntity.getDayCount())
                    .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                    .build();
        }

        //构建聚合对象
        return CreateQuotaOrderAggregate.builder()
                .userId(userId)
                .activityId(activityId)
                .activityAccountEntity(activityAccountEntity)
                .activityAccountDayEntity(activityAccountDayEntity)
                .isExistAccountDay(isExistAccountDay)
                .activityAccountMonthEntity(activityAccountMonthEntity)
                .isExistAccountMonth(isExistAccountMonth)
                .build();
    }

    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date now) {
        ActivityEntity activityEntity = repository.queryRaffleActivityByActivityId(activityId);
        //构建订单
        return UserRaffleOrderEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .activityName(activityEntity.getActivityName())
                .strategyId(activityEntity.getStrategyId())
                .orderId(RandomStringUtils.randomNumeric(12))
                .orderTime(now)
                .orderState(UserRaffleOrderStateVO.create)
                .build();
    }
}
