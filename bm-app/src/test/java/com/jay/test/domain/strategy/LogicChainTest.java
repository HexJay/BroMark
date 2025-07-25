package com.jay.test.domain.strategy;

import com.jay.domain.strategy.service.armory.IStrategyArmory;
import com.jay.domain.strategy.service.rule.chain.ILogicChain;
import com.jay.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
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
 * @date 2025/7/4 16:12
 * @description
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogicChainTest {

    @Resource
    private DefaultChainFactory defaultChainFactory;

    @Resource
    RuleWeightLogicChain ruleWeightLogicChain;

    @Resource
    private IStrategyArmory strategyArmory;

    @Before
    public void before() {
        log.info("装配100001结果：{}",strategyArmory.assembleLotteryStrategy(100001L));
        log.info("装配100002结果：{}",strategyArmory.assembleLotteryStrategy(100002L));
        log.info("装配100003结果：{}",strategyArmory.assembleLotteryStrategy(100003L));
    }

    @Test
    public void test_LogicChain_rule_blacklist() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        Integer awardId = logicChain.logic("user001", 100001L).getAwardId();
        log.info("测试结果：{}", awardId);
    }

    @Test
    public void test_LogicChain_rule_weight() {
        // 通过反射 mock 规则中的值
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4900L);
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        Integer awardId = logicChain.logic("Jay", 100001L).getAwardId();
        log.info("测试结果：{}", awardId);
    }

    @Test
    public void test_LogicChain_rule_default() {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(100001L);
        Integer awardId = logicChain.logic("Jay", 100001L).getAwardId();
        log.info("测试结果：{}", awardId);
    }
}
