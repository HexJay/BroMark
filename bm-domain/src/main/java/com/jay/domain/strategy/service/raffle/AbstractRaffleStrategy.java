package com.jay.domain.strategy.service.raffle;

import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.model.entity.StrategyEntity;
import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.IRaffleStrategy;
import com.jay.domain.strategy.service.armory.IStrategyDispatch;
import com.jay.domain.strategy.service.rule.factory.DefaultLogicFactory;
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

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch dispatch) {
        this.repository = repository;
        this.dispatch = dispatch;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity entity) {
        // 1.参数校验
        String userId = entity.getUserId();
        Long strategyId = entity.getStrategyId();
        if (StringUtils.isBlank(userId) || strategyId == null) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.查询用户的策略
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);

        // 3.抽奖前 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBefore> ruleAction =
                this.doCheckRaffleBeforeLogic(entity, strategyEntity.ruleModels());

        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleAction.getCode())) {
            // 黑名单返回固定奖品ID
            if (DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleAction.getRuleModel())) {
                return RaffleAwardEntity.builder()
                        .awardId(ruleAction.getData().getAwardId())
                        .build();
            }
            // 指定范围抽奖（权重规则），根据范围返回抽到的奖品
            if (DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode().equals(ruleAction.getRuleModel())) {
                RuleActionEntity.RaffleBefore raffleBefore = ruleAction.getData();
                String ruleWeightValueKey = raffleBefore.getRuleWeightKey();
                Integer awardId = dispatch.getRandomAwardId(strategyId, ruleWeightValueKey);
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();

            }
        }

        // 4.默认抽奖流程
        Integer awardId = dispatch.getRandomAwardId(strategyId);

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBefore> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
