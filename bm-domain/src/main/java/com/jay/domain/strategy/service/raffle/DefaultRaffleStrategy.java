package com.jay.domain.strategy.service.raffle;

import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.model.entity.RuleActionEntity;
import com.jay.domain.strategy.model.entity.RuleMatterEntity;
import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.domain.strategy.service.AbstractRaffleStrategy;
import com.jay.domain.strategy.service.armory.IStrategyDispatch;
import com.jay.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.jay.domain.strategy.service.rule.filter.ILogicFilter;
import com.jay.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jay
 * @date 2025/6/30 22:05
 * @description
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    @Resource
    private DefaultLogicFactory logicFactory;

    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch dispatch, DefaultChainFactory chainFactory) {
        super(repository, dispatch, chainFactory);
    }


    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBefore> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {

        if (logics == null || logics.length == 0){
            return RuleActionEntity.<RuleActionEntity.RaffleBefore>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        Map<String, ILogicFilter<RuleActionEntity.RaffleBefore>> logicFilterGroup = logicFactory.openLogicFilter();

        // 1.优先过滤黑名单规则
        String ruleBlackList = Arrays.stream(logics)
                .filter(str -> str.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        if (StringUtils.isNotBlank(ruleBlackList)) {
            ILogicFilter<RuleActionEntity.RaffleBefore> logicFilter = logicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());

            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .userId(raffleFactorEntity.getUserId())
                    .strategyId(raffleFactorEntity.getStrategyId())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                    .build();

            RuleActionEntity<RuleActionEntity.RaffleBefore> ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }
        }

        // 2.顺序过滤剩余规则
        List<String> ruleList = Arrays.stream(logics)
                .filter(s -> !s.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBefore> ruleActionEntity = null;
        for (String ruleModel : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBefore> logicFilter = logicFilterGroup.get(ruleModel);

            RuleMatterEntity ruleMatterEntity = RuleMatterEntity.builder()
                    .userId(raffleFactorEntity.getUserId())
                    .strategyId(raffleFactorEntity.getStrategyId())
                    .awardId(raffleFactorEntity.getAwardId())
                    .ruleModel(ruleModel)
                    .build();

            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode()))
                return ruleActionEntity;
        }

        return ruleActionEntity;
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleDuring> doCheckRaffleDuringLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {

        if (logics == null || logics.length == 0){
            return RuleActionEntity.<RuleActionEntity.RaffleDuring>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        Map<String, ILogicFilter<RuleActionEntity.RaffleDuring>> logicFilterGroup = logicFactory.openLogicFilter();

        RuleActionEntity<RuleActionEntity.RaffleDuring> ruleActionEntity = null;
        for (String ruleModel : logics) {
            ILogicFilter<RuleActionEntity.RaffleDuring> logicFilter = logicFilterGroup.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(raffleFactorEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖中规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), ruleModel, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode()))
                return ruleActionEntity;
        }

        return ruleActionEntity;
    }
}
