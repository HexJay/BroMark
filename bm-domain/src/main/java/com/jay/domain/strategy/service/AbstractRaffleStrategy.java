package com.jay.domain.strategy.service;

import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.armory.IStrategyDispatch;
import com.jay.domain.strategy.service.rule.chain.ILogicChain;
import com.jay.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.jay.types.enums.ResponseCode;
import com.jay.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jay
 * @date 2025/6/30 16:40
 * @description 抽奖策略抽象类
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    // 策略仓储服务 -> 提供数据
    protected IStrategyRepository repository;
    // 策略调度服务 -> 负责抽奖处理，通过新增接口的方式，隔离职责，使用方不需要关心抽奖的初始化
    protected IStrategyDispatch dispatch;

    private DefaultChainFactory chainFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch dispatch, DefaultChainFactory chainFactory) {
        this.repository = repository;
        this.dispatch = dispatch;
        this.chainFactory = chainFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity entity) {
        // 1.参数校验
        String userId = entity.getUserId();
        Long strategyId = entity.getStrategyId();
        if (StringUtils.isBlank(userId) || strategyId == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 2.责任链抽奖
        ILogicChain logicChain = chainFactory.openLogicChain(strategyId);
        Integer awardId = logicChain.logic(userId, strategyId);

        // 3.抽奖中 - 规则过滤（拿到奖品ID后）
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModel(strategyId, awardId);
        RuleActionEntity<RuleActionEntity.RaffleDuring> ruleActionDuring = this.doCheckRaffleDuringLogic(RaffleFactorEntity.builder()
                .userId(userId)
                .strategyId(strategyId)
                .awardId(awardId)
                .build(), strategyAwardRuleModelVO.raffleDuringRuleModelList());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionDuring.getCode())) {
            log.info("[临时日志] 抽奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励");
            return RaffleAwardEntity.builder()
                    .awardDesc("抽奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励")
                    .build();
        }


        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBefore> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleDuring> doCheckRaffleDuringLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
