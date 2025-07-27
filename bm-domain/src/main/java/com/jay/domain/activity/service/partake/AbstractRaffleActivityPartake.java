package com.jay.domain.activity.service.partake;


import com.jay.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.jay.domain.activity.model.entity.ActivityEntity;
import com.jay.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.jay.domain.activity.model.entity.UserRaffleOrderEntity;
import com.jay.domain.activity.model.vo.ActivityStateVO;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.domain.activity.service.IRaffleActivityPartakeService;
import com.jay.types.enums.ResponseCode;
import com.jay.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/22 21:46
 * @description 抽奖活动参与抽奖类
 */
@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    protected final IActivityRepository repository;

    protected AbstractRaffleActivityPartake(IActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        // 1.基础信息
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date now = new Date();

        // 2.活动查询
        ActivityEntity activityEntity = repository.queryRaffleActivityByActivityId(activityId);

        // 校验：活动状态
        if (!ActivityStateVO.open.equals(activityEntity.getState())) {
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        // 校验：活动日期
        if (activityEntity.getBeginDateTime().after(now) || activityEntity.getEndDateTime().before(now)) {
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        // 2.查询未使用的活动参与订单记录
        UserRaffleOrderEntity userRaffleOrderEntity = repository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        if (userRaffleOrderEntity != null) {
            log.info("创建参与活动订单【已存在未消费】userId:{} activityId:{} userRaffleOrderEntity={}", userId, activityId, userRaffleOrderEntity);
            return userRaffleOrderEntity;
        }

        // 3.账户额度过滤，返回账户构建对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = this.doFilterAccount(userId, activityId, now);

        // 4.构建订单
        UserRaffleOrderEntity userRaffleOrder = this.buildUserRaffleOrder(userId, activityId, now);

        // 5.填充抽奖单实体对象
        createQuotaOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);

        // 6.保存聚合对象-一个领域内的一个聚合是一个事务操作
        repository.saveCreatePartakeOrderAggregate(createQuotaOrderAggregate);

        return userRaffleOrder;
    }

    @Override
    public UserRaffleOrderEntity createOrder(String userId, Long activityId) {
        return createOrder(PartakeRaffleActivityEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
    }

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date now);

    protected abstract CreateQuotaOrderAggregate doFilterAccount(String userId, Long activityId, Date now);
}
