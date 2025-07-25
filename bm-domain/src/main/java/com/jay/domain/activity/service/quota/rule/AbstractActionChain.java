package com.jay.domain.activity.service.quota.rule;


/**
 * @author Jay
 * @date 2025/7/17 15:11
 * @description 填充责任链抽象类
 */
public abstract class AbstractActionChain implements IActionChain {

    private IActionChain next;

    @Override
    public IActionChain appendNext(IActionChain next) {
        this.next = next;
        return next;
    }

    @Override
    public IActionChain next() {
        return next;
    }
}
