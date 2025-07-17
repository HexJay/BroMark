package com.jay.domain.activity.service.rule.factory;


import com.jay.domain.activity.repository.IActivityRepository;
import com.jay.domain.activity.service.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Jay
 * @date 2025/7/17 15:04
 * @description 活动责任链工厂
 */
@Service
public class DefaultActivityChainFactory {

    private final IActionChain actionChain;

    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainMap) {
        actionChain = actionChainMap.get(ActionModel.ACTIVITY_BASE_ACTION.code);
        actionChain.appendNext(actionChainMap.get(ActionModel.ACTIVITY_SKU_STOCK_ACTION.code));
    }

    public IActionChain openActionChain(){
        return actionChain;
    }

    @Getter
    @AllArgsConstructor
    private enum ActionModel {

        ACTIVITY_BASE_ACTION("activity_base_action", "活动的库存、时间校验"),
        ACTIVITY_SKU_STOCK_ACTION("activity_sku_stock_action","活动sku库存"),
        ;

        private final String code;
        private final String info;
    }
}
