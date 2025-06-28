package com.jay.test.infrastructure;

import com.jay.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/6/27 22:58
 * @description
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyRepositoryTest {
    @Resource
    private IStrategyRepository repository;

    @Test
    public void assembleLotteryStrategyTest(){
        repository.queryStrategyAwardList(100002L);
    }

}
