package com.jay.test.domain;

import com.jay.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/6/27 22:21
 * @description
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {

    @Resource
    private IStrategyArmory strategyArmory;

    @Test
    public void strategyArmory() {
        strategyArmory.assembleLotteryStrategy(100002L);
    }

    @Test
    public void test_getAssembleRandomVal() {
        log.info("测试结果：{} - 奖品ID值",strategyArmory.getRandomAwardId(100002L));
        log.info("测试结果：{} - 奖品ID值",strategyArmory.getRandomAwardId(100002L));

    }
}
