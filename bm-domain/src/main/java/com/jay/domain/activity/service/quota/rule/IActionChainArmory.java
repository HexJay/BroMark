package com.jay.domain.activity.service.quota.rule;


/**
 * @author Jay
 * @date 2025/7/17 15:07
 * @description 抽奖活动责任链组装规则接口
 */
public interface IActionChainArmory {

    IActionChain next();
    IActionChain appendNext(IActionChain next);
}
