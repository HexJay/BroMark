package com.jay.domain.activity.service;


import com.jay.domain.activity.ISkuStock;
import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.entity.*;
import com.jay.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.jay.domain.activity.model.vo.OrderStateVO;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Jay
 * @date 2025/7/16 16:47
 * @description TODO
 */
@Service
public class RaffleActivityService extends AbstractRaffleActivity implements ISkuStock {

    public RaffleActivityService(IActivityRepository repository, DefaultActivityChainFactory defaultActivityChainFactory) {
        super(repository, defaultActivityChainFactory);
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate aggregate) {
        repository.doSaveOrder(aggregate);
    }

    @Override
    protected CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        // 订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(skuRechargeEntity.getUserId());
        activityOrderEntity.setSku(skuRechargeEntity.getSku());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());
        // 公司里一般会有专门的雪花算法UUID服务，我们这里直接生成个12位就可以了。
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setState(OrderStateVO.completed);
        activityOrderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());

        // 构建聚合对象
        return CreateOrderAggregate.builder()
                .userId(skuRechargeEntity.getUserId())
                .activityId(activitySkuEntity.getActivityId())
                .totalCount(activityCountEntity.getTotalCount())
                .dayCount(activityCountEntity.getDayCount())
                .monthCount(activityCountEntity.getMonthCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void clearQueue() {
        repository.clearQueue();
    }

    @Override
    public void updateActivitySkuStock(Long Sku) {
        repository.updateActivitySkuStock(Sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        repository.clearActivitySkuStock(sku);
    }
}
