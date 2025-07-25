package com.jay.infrastructure.adapter.repository;


import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jay.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.jay.domain.activity.model.aggregate.CreateOrderAggregate;
import com.jay.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.jay.domain.activity.model.entity.*;
import com.jay.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.jay.domain.activity.model.vo.ActivityStateVO;
import com.jay.domain.activity.model.vo.UserRaffleOrderStateVO;
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
    @Resource
    private IRaffleActivityAccountMonthDao raffleActivityAccountMonthDao;
    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;
    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;

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

    @Override
    public void saveCreatePartakeOrderAggregate(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        try {
            String userId = createQuotaOrderAggregate.getUserId();
            Long activityId = createQuotaOrderAggregate.getActivityId();
            ActivityAccountEntity activityAccountEntity = createQuotaOrderAggregate.getActivityAccountEntity();
            ActivityAccountDayEntity activityAccountDayEntity = createQuotaOrderAggregate.getActivityAccountDayEntity();
            ActivityAccountMonthEntity activityAccountMonthEntity = createQuotaOrderAggregate.getActivityAccountMonthEntity();
            UserRaffleOrderEntity userRaffleOrderEntity = createQuotaOrderAggregate.getUserRaffleOrderEntity();

            // 统一切换路由，以下事务内的所有操作，都走一个路由
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    // 1.更新总账户
                    int totalCount = raffleActivityAccountDao.updateActivityAccountSubtractionQuota(
                            RaffleActivityAccount.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .build()
                    );

                    if (totalCount != 1) {
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录 - 更新总账户额度不足，异常 userId:{} activityId:{}", userId, activityId);
                        throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                    }

                    // 2. 创建或更新月账户，true - 存在则更新，false - 不存在则插入
                    if (createQuotaOrderAggregate.isExistAccountMonth()) {
                        int updateMonthCount = raffleActivityAccountMonthDao.updateActivityAccountMonthSubtractionQuota(
                                RaffleActivityAccountMonth.builder()
                                        .userId(activityAccountMonthEntity.getUserId())
                                        .activityId(activityAccountMonthEntity.getActivityId())
                                        .month(activityAccountMonthEntity.getMonth())
                                        .build()
                        );

                        if (updateMonthCount != 1) {
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录 - 更新月账户额度不足，异常 userId:{} activityId:{} month: {}", userId, activityId, activityAccountMonthEntity.getMonth());
                            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                        }
                    } else {
                        // 新创建月账户，则更新总账表中月镜像额度
                        raffleActivityAccountMonthDao.insertActivityAccountMonth(RaffleActivityAccountMonth.builder()
                                .userId(activityAccountMonthEntity.getUserId())
                                .activityId(activityAccountMonthEntity.getActivityId())
                                .month(activityAccountMonthEntity.getMonth())
                                .monthCount(activityAccountMonthEntity.getMonthCount())
                                .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus() - 1)
                                .build());

                        raffleActivityAccountDao.updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount.builder()
                                .userId(activityAccountMonthEntity.getUserId())
                                .activityId(activityAccountMonthEntity.getActivityId())
                                .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                                .build());
                    }

                    // 3. 创建或更新日账户，true - 存在则更新，false - 不存在则插入
                    if (createQuotaOrderAggregate.isExistAccountDay()) {
                        int updateDayCount = raffleActivityAccountDayDao.updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay.builder()
                                .userId(activityAccountDayEntity.getUserId())
                                .activityId(activityAccountDayEntity.getActivityId())
                                .day(activityAccountDayEntity.getDay())
                                .build());
                        if (updateDayCount != 1) {
                            status.setRollbackOnly();
                            log.warn("写入创建参与活动记录 - 更新日账户额度不足，异常 userId:{} activityId:{} day: {}", userId, activityId, activityAccountDayEntity.getDay());
                            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                        }
                    } else {
                        // 新创建日账户，则更新总账表中日镜像额度
                        raffleActivityAccountDayDao.insertActivityAccountDay(RaffleActivityAccountDay.builder()
                                .userId(activityAccountDayEntity.getUserId())
                                .activityId(activityAccountDayEntity.getActivityId())
                                .day(activityAccountDayEntity.getDay())
                                .dayCount(activityAccountDayEntity.getDayCount())
                                .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus() - 1)
                                .build());

                        raffleActivityAccountDao.updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount.builder()
                                .userId(activityAccountDayEntity.getUserId())
                                .activityId(activityAccountDayEntity.getActivityId())
                                .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                                .build()
                        );
                    }

                    // 4. 写入参与活动订单
                    userRaffleOrderDao.insert(UserRaffleOrder.builder()
                            .userId(userRaffleOrderEntity.getUserId())
                            .activityId(userRaffleOrderEntity.getActivityId())
                            .activityName(userRaffleOrderEntity.getActivityName())
                            .strategyId(userRaffleOrderEntity.getStrategyId())
                            .orderId(userRaffleOrderEntity.getOrderId())
                            .orderTime(userRaffleOrderEntity.getOrderTime())
                            .orderState(userRaffleOrderEntity.getOrderState().getCode())
                            .build());
                    return 1;

                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入创建参与活动记录 - 唯一索引冲突 userId:{} activityId:{}", userId, activityId);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }
    }

    @Override
    public UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        UserRaffleOrder userRaffleOrderReq = UserRaffleOrder.builder()
                .userId(partakeRaffleActivityEntity.getUserId())
                .activityId(partakeRaffleActivityEntity.getActivityId())
                .build();

        UserRaffleOrder userRaffleOrderRes = userRaffleOrderDao.queryNoUsedRaffleOrder(userRaffleOrderReq);
        if (userRaffleOrderRes == null) return null;

        return UserRaffleOrderEntity.builder()
                .userId(userRaffleOrderRes.getUserId())
                .activityId(userRaffleOrderRes.getActivityId())
                .activityName(userRaffleOrderRes.getActivityName())
                .strategyId(userRaffleOrderRes.getStrategyId())
                .orderId(userRaffleOrderRes.getOrderId())
                .orderTime(userRaffleOrderRes.getOrderTime())
                .orderState(UserRaffleOrderStateVO.valueOf(userRaffleOrderRes.getOrderState()))
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDay(String userId, Long activityId, String day) {
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(
                RaffleActivityAccountDay.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .day(day)
                        .build()
        );
        if (raffleActivityAccountDay == null) return null;
        return ActivityAccountDayEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .day(day)
                .dayCount(raffleActivityAccountDay.getDayCount())
                .dayCountSurplus(raffleActivityAccountDay.getDayCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonth(String userId, Long activityId, String month) {
        RaffleActivityAccountMonth raffleActivityAccountMonth = raffleActivityAccountMonthDao.queryActivityAccountMonth(
                RaffleActivityAccountMonth.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .month(month)
                        .build()
        );
        if (raffleActivityAccountMonth == null) return null;
        return ActivityAccountMonthEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .month(month)
                .monthCount(raffleActivityAccountMonth.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonth.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountEntity queryActivityAccount(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccount(
                RaffleActivityAccount.builder()
                        .userId(userId)
                        .activityId(activityId)
                        .build()
        );
        if (raffleActivityAccount == null) return null;
        return ActivityAccountEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .totalCount(raffleActivityAccount.getTotalCount())
                .totalCountSurplus(raffleActivityAccount.getTotalCountSurplus())
                .dayCount(raffleActivityAccount.getDayCount())
                .dayCountSurplus(raffleActivityAccount.getDayCountSurplus())
                .monthCount(raffleActivityAccount.getMonthCount())
                .monthCountSurplus(raffleActivityAccount.getMonthCountSurplus())
                .build();
    }
}
