package com.jay.domain.activity.service;


import com.jay.domain.activity.model.entity.ActivityCountEntity;
import com.jay.domain.activity.model.entity.ActivityEntity;
import com.jay.domain.activity.model.entity.ActivitySkuEntity;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.domain.activity.service.rule.factory.DefaultActivityChainFactory;

/**
 * @author Jay
 * @date 2025/7/17 15:41
 * @description 抽奖活动的支撑类，专门用于进行数据的查询
 */
public class RaffleActivitySupport {

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    protected IActivityRepository repository;

    public RaffleActivitySupport(IActivityRepository repository, DefaultActivityChainFactory defaultActivityChainFactory) {
        this.repository = repository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku) {
        return repository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        return repository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        return repository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }
}
