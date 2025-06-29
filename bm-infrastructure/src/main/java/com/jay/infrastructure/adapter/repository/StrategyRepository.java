package com.jay.infrastructure.adapter.repository;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.jay.domain.strategy.model.entity.StrategyAwardEntity;
import com.jay.domain.strategy.model.entity.StrategyEntity;
import com.jay.domain.strategy.model.entity.StrategyRuleEntity;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.infrastructure.dao.IStrategyAwardDao;
import com.jay.infrastructure.dao.IStrategyDao;
import com.jay.infrastructure.dao.IStrategyRuleDao;
import com.jay.infrastructure.dao.po.Strategy;
import com.jay.infrastructure.dao.po.StrategyAward;
import com.jay.infrastructure.dao.po.StrategyRule;
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
    private IStrategyRuleDao strategyRuleDao;

    @Resource
    private IStrategyDao strategyDao;

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
                .map(strategyAward -> BeanUtil.copyProperties(strategyAward, StrategyAwardEntity.class))
                .collect(Collectors.toList());

        // 6.缓存JSON数据
        redisService.setList(key, entityList);
        return entityList;
    }

    @Override
    public void storeStrategyAwardSearchRateTables(String key, Integer rateRange, HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTables) {
        // 1.存储抽奖策略单位数量，用于生成范围以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);

        // 2.存储概率查找表
        RMap<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffledStrategyAwardSearchRateTables);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 1.从缓存获取
        String key = Constants.RedisKey.STRATEGY_KEY + strategyId;
        String json = redisService.getValue(key);
        // 2.判空
        if (null != json) {
            return JSON.parseObject(json, StrategyEntity.class);
        }
        // 3.查数据库
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        StrategyEntity strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();

        // 4.缓存数据
        redisService.setValue(key, JSON.toJSONString(strategyEntity));
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule query = new StrategyRule();
        query.setStrategyId(strategyId);
        query.setRuleModel(ruleModel);

        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(query);
        return BeanUtil.copyProperties(strategyRule, StrategyRuleEntity.class);
    }
}
