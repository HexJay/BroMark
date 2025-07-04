package com.jay.test.domain;

import com.alibaba.fastjson2.JSON;
import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.service.IRaffleStrategy;
import com.jay.domain.strategy.service.armory.IStrategyArmory;
import com.jay.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import com.jay.domain.strategy.service.rule.filter.impl.RuleLockLogicFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/7/1 14:49
 * @description
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private RuleLockLogicFilter ruleLockLogicFilter;

    @Before
    public void before() {
        log.info("装配100001结果：{}",strategyArmory.assembleLotteryStrategy(100001L));
        log.info("装配100002结果：{}",strategyArmory.assembleLotteryStrategy(100002L));
        log.info("装配100003结果：{}",strategyArmory.assembleLotteryStrategy(100003L));
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4500L);
        ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleCount", 10L);
    }

    @Test
    public void performRaffle() {
        RaffleFactorEntity factorEntity = RaffleFactorEntity.builder()
                .strategyId(100001L)
                .userId("Jay")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factorEntity);

        log.info("请求参数：{}", JSON.toJSONString(factorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void performRaffle_blackList() {
        RaffleFactorEntity factorEntity = RaffleFactorEntity.builder()
                .strategyId(100001L)
                .userId("user001")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factorEntity);

        log.info("请求参数：{}", JSON.toJSONString(factorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void performRaffle_lock() {
        RaffleFactorEntity factorEntity = RaffleFactorEntity.builder()
                .strategyId(100003L)
                .userId("Jay")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factorEntity);

        log.info("请求参数：{}", JSON.toJSONString(factorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }
}
