package com.jay.domain.strategy.service.armory;

import com.jay.domain.strategy.model.entity.StrategyAwardEntity;
import com.jay.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jay
 * @date 2025/6/27 19:53
 * @description 策略装配工厂，负责初始化策略计算
 */
@Slf4j
@Service
public class StrategyArmory implements IStrategyArmory {

    @Resource
    private IStrategyRepository repository;

    @Override
    public void assembleLotteryStrategy(Long strategyId) {
        // 1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        // 2. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        // 3.获取概率值的总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4.得到单位大小
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 5.填充概率分布
        ArrayList<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            // 计算出每个概率存放到查找表的数量，循环填充
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 6.乱序
        Collections.shuffle(strategyAwardSearchRateTables);

        // 7.转换成Hash表
        HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTables = new HashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffledStrategyAwardSearchRateTables.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 8.存入Redis
        repository.storeStrategyAwardSearchRateTables(strategyId, shuffledStrategyAwardSearchRateTables.size(), shuffledStrategyAwardSearchRateTables);
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        // log.info("random is {}", RANDOM.nextInt(rateRange));
        return repository.getStrategyAwardAssemble(strategyId, RANDOM.nextInt(rateRange));
    }
}
