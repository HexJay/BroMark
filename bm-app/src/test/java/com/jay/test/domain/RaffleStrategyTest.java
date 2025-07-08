package com.jay.test.domain;

import com.alibaba.fastjson2.JSON;
import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.service.IRaffleStrategy;
import com.jay.domain.strategy.service.armory.IStrategyArmory;
import com.jay.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
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


    @Before
    public void before() {
        log.info("装配100001结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
        // log.info("装配100002结果：{}", strategyArmory.assembleLotteryStrategy(100002L));
        // log.info("装配100003结果：{}", strategyArmory.assembleLotteryStrategy(100003L));
        log.info("装配100006结果：{}", strategyArmory.assembleLotteryStrategy(100006L));
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4900L);
    }

    @Test
    public void performRaffle() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("Jay")
                .strategyId(100006L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
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
