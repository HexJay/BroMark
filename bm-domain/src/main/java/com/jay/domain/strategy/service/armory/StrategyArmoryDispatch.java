package com.jay.domain.strategy.service.armory;

import com.jay.domain.strategy.model.entity.StrategyAwardEntity;
import com.jay.domain.strategy.model.entity.StrategyEntity;
import com.jay.domain.strategy.model.entity.StrategyRuleEntity;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.types.common.Constants;
import com.jay.types.enums.ResponseCode;
import com.jay.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author Jay
 * @date 2025/6/27 19:53
 * @description 策略装配工厂，负责初始化策略计算
 */
@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    private static final SecureRandom RANDOM = new SecureRandom();
    @Resource
    private IStrategyRepository repository;

    @Override
    public Boolean assembleLotteryStrategy(Long strategyId) {
        // 1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);

        // 2.缓存奖品库存
        for (StrategyAwardEntity award : strategyAwardEntities) {
            Integer awardId = award.getAwardId();
            Integer awardCount = award.getAwardCount();
            cacheStrategyAwardCount(strategyId, awardId, awardCount);
        }

        // 3.装配概率分布
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntities);
        // 4.配置抽奖范围策略 - 适用于rule_weight配置
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        // 5.1.没有范围配置
        if (ruleWeight == null) return true;

        // 6.查询rule_weight配置
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId, ruleWeight);
        if (strategyRuleEntity == null)
            throw new AppException(
                    ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),
                    ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo()
            );

        // 7.解析奖品范围
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();
        for (String key : keys) {
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            // 深拷贝
            ArrayList<StrategyAwardEntity> awardListDeepClone = new ArrayList<>(strategyAwardEntities);
            // 剔除当前范围外的Award
            awardListDeepClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));
            // 装配当前抽奖范围
            assembleLotteryStrategy(String.valueOf(strategyId).concat(":").concat(key), awardListDeepClone);
        }

        return true;
    }

    @Override
    public Boolean assembleLotteryStrategyByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return assembleLotteryStrategy(strategyId);
    }

    /**
     * 缓存奖品的库存
     *
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @param awardCount 奖品库存
     */
    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.COLON + awardId;
        repository.cacheStrategyAwardCount(cacheKey, awardCount);
    }

    /**
     * 装配概率分布
     *
     * @param key
     * @param strategyAwardEntities
     */
    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        // 1. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        // 2.获取概率值的总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3.得到单位大小
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 4.填充概率分布
        ArrayList<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            // 计算出每个概率存放到查找表的数量，循环填充
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 5.乱序
        Collections.shuffle(strategyAwardSearchRateTables);

        // 6.转换成Hash表
        HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTables = new HashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffledStrategyAwardSearchRateTables.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 7.存入Redis
        repository.storeStrategyAwardSearchRateTables(key, shuffledStrategyAwardSearchRateTables.size(), shuffledStrategyAwardSearchRateTables);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        // log.info("random is {}", RANDOM.nextInt(rateRange));
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId), RANDOM.nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, Long ruleWeightKey) {
        String key = String.valueOf(strategyId).concat(":").concat(String.valueOf(ruleWeightKey));
        int rateRange = repository.getRateRange(key);
        return repository.getStrategyAwardAssemble(key, RANDOM.nextInt(rateRange));
    }

    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.COLON + awardId;
        return repository.subtractionAwardStock(cacheKey);
    }
}
