package com.jay.infrastructure.adapter.repository;

import com.alibaba.fastjson2.JSON;
import com.jay.domain.strategy.model.entity.StrategyAwardEntity;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.infrastructure.dao.IStrategyAwardDao;
import com.jay.infrastructure.dao.po.StrategyAward;
import com.jay.infrastructure.redis.IRedisService;
import com.jay.types.common.Constants;
import org.redisson.api.RMap;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jay
 * @date 2025/6/27 19:57
 * @description 策略仓储实现
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 1.设置Key
        String key = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        // 2.从Redis查询
        List<StrategyAwardEntity> list = redisService.getList(key, StrategyAwardEntity.class);
        // 3.查询成功，直接返回
        if (list != null && !list.isEmpty()) {
            return list;
        }
        // 4.查询缓存失败，从数据库查
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);

        // 5.转换为实体列表
        List<StrategyAwardEntity> entityList = strategyAwards.stream()
                .map(strategyAward -> {
                    StrategyAwardEntity entity = new StrategyAwardEntity();
                    BeanUtils.copyProperties(strategyAward, entity);
                    return entity;
                })
                .collect(Collectors.toList());

        // 6.缓存JSON数据
        redisService.setList(key,entityList);

        return entityList;
    }

    @Override
    public void storeStrategyAwardSearchRateTables(Long strategyId, Integer rateRange, HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTables) {
        // 1.存储抽奖策略范围值，如10000，用于生成范围以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);

        // 2.存储概率查找表
        RMap<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffledStrategyAwardSearchRateTables);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }
}
