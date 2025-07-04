package com.jay.domain.strategy.service.rule.chain;

/**
 * @author Jay
 * @date 2025/7/4 14:25
 * @description
 */
public abstract class AbstractLogicChain implements ILogicChain {

    private ILogicChain next;

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next() {
        return next;
    }

    protected abstract String ruleModel();
}
