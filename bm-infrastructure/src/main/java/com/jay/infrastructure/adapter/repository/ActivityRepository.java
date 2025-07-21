package com.jay.infrastructure.adapter.repository;


import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jay.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.entity.ActivityCountEntity;
import com.jay.domain.activity.model.entity.ActivityEntity;
import com.jay.domain.activity.model.entity.ActivityOrderEntity;
import com.jay.domain.activity.model.entity.ActivitySkuEntity;
import com.jay.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.jay.domain.activity.model.vo.ActivityStateVO;
import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.infrastructure.dao.*;
import com.jay.infrastructure.dao.po.*;
import com.jay.infrastructure.event.EventPublisher;
import com.jay.infrastructure.redis.IRedisService;
import com.jay.types.common.Constants;
import com.jay.types.enums.ResponseCode;
import com.jay.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay
 * @date 2025/7/16 16:44
 * @description 活动仓储服务
 */
@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {
    @Resource
    private IRedisService redisService;
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;
    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;
    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;
    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;

    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;

    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_KEY + Constants.COLON + sku;
        String json = redisService.getValue(cacheKey);
        if (StrUtil.isNotEmpty(json)) {
            return JSON.parseObject(json, ActivitySkuEntity.class);
        }
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryRaffleActivitySku(sku);
        ActivitySkuEntity activitySkuEntity = ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
        redisService.setValue(cacheKey, JSON.toJSONString(activitySkuEntity));
        return activitySkuEntity;
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        String json = redisService.getValue(cacheKey);
        if (StringUtils.isNotBlank(json)) {
            return JSONObject.parseObject(json, ActivityEntity.class);
        }
        // 从库中获取数据
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        ActivityEntity activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();

        redisService.setValue(cacheKey, JSON.toJSONString(activityEntity));
        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        String json = redisService.getValue(cacheKey);
        if (StringUtils.isNotBlank(json)) return JSONObject.parseObject(json, ActivityCountEntity.class);
        // 从库中获取数据
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        ActivityCountEntity activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey, JSON.toJSONString(activityCountEntity));
        return activityCountEntity;
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate aggregate) {
        // 订单对象
        ActivityOrderEntity activityOrderEntity = aggregate.getActivityOrderEntity();
        RaffleActivityOrder order = BeanUtil.copyProperties(activityOrderEntity, RaffleActivityOrder.class);
        order.setState(activityOrderEntity.getState().getCode());

        // 次数账户对象
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(aggregate.getUserId());
        raffleActivityAccount.setActivityId(aggregate.getActivityId());
        raffleActivityAccount.setTotalCount(aggregate.getTotalCount());
        raffleActivityAccount.setDayCount(aggregate.getDayCount());
        raffleActivityAccount.setMonthCount(aggregate.getMonthCount());
        raffleActivityAccount.setTotalCountSurplus(aggregate.getTotalCount());
        raffleActivityAccount.setDayCountSurplus(aggregate.getDayCount());
        raffleActivityAccount.setMonthCountSurplus(aggregate.getMonthCount());

        try {
            // 以用户ID作为切分键，通过dbRouter设定路由
            dbRouter.doRouter(aggregate.getUserId());
            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    // 1.写入订单
                    raffleActivityOrderDao.insert(order);
                    // 2.更新账户
                    int count = raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                    // 3.创建账户，更新账户为0，账户不存在，创建新账户。
                    if (count == 0) {
                        raffleActivityAccountDao.insert(raffleActivityAccount);
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入订单记录，唯一索引冲突 userId:{} activityId:{} sku:{}", aggregate.getUserId(), aggregate.getActivityId(), activityOrderEntity.getSku());
                    throw new AppException(ResponseCode.UN_ERROR.getCode());
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public void cacheActivitySkuStockCount(String key, Integer stockCount) {
        if (redisService.isExists(key)) return;
        redisService.setAtomicLong(key, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String key, Date endDateTime) {
        long surplus = redisService.decr(key);
        if (surplus == 0) {
            // 库存消耗完，发送MQ消息，更新数据库库存
            eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(), activitySkuStockZeroMessageEvent.buildEventMessage(sku));
            return false;
        } else if (surplus < 0) {
            // 库存小于0，恢复为0个
            redisService.setAtomicLong(key, 0L);
            return false;
        }
        // 1.用key decr后的值和key组成库存锁
        // 2.加锁兜底，所有可用库存key都加锁了
        // 3.设置加锁过期时间为活动到期 + 延迟 1 天
        String lockKey = key + Constants.COLON + surplus;
        long expireMills = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean lock = redisService.setNx(lockKey, expireMills, TimeUnit.MILLISECONDS);
        if (!lock) {
            log.info("活动sku库存加锁失败 {}", lockKey);
        }
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String key = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String key = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        return blockingQueue.poll();
    }

    @Override
    public void clearQueue() {
        String key = Constants.RedisKey.ACTIVITY_SKU_STOCK_QUEUE_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(key);
        blockingQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuDao.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuDao.clearActivitySkuStock(sku);
    }
}
