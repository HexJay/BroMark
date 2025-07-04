package com.jay.domain.strategy.service.rule.chain;

/**
 * @author Jay
 * @date 2025/7/4 16:01
 * @description 责任链装配接口
 */
public interface ILogicChainArmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();
}
