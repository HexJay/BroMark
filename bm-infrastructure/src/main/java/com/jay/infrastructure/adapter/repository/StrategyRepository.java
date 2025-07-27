package com.jay.infrastructure.adapter.repository;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.jay.domain.strategy.model.entity.StrategyAwardEntity;
import com.jay.domain.strategy.model.entity.StrategyEntity;
import com.jay.domain.strategy.model.entity.StrategyRuleEntity;
import com.jay.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.jay.domain.strategy.model.vo.StrategyAwardRuleModelVO;
import com.jay.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import com.jay.domain.strategy.model.vo.tree.RuleLimitTypeVO;
import com.jay.domain.strategy.model.vo.tree.RuleTreeNodeLineVO;
import com.jay.domain.strategy.model.vo.tree.RuleTreeNodeVO;
import com.jay.domain.strategy.model.vo.tree.RuleTreeVO;
import com.jay.domain.strategy.repository.IStrategyRepository;
import com.jay.infrastructure.dao.*;
import com.jay.infrastructure.dao.po.*;
import com.jay.infrastructure.redis.IRedisService;
import com.jay.types.common.Constants;
import com.jay.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jay.types.enums.ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY;

/**
 * @author Jay
 * @date 2025/6/27 19:57
 * @description 策略仓储实现
 */
@Slf4j
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private IStrategyRuleDao strategyRuleDao;
    @Resource
    private IStrategyDao strategyDao;
    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IRuleTreeDao ruleTreeDao;
    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;
    @Resource
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 1.设置Key
        String key = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
        // 2.从Redis查询
        List<StrategyAwardEntity> list = redisService.getList(key, StrategyAwardEntity.class);
        // 3.查询成功，直接返回
        if (list != null && !list.isEmpty()) {
            return list;
        }
        // 4.查询缓存失败，从数据库查
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);

        // 5.转换为实体列表
        List<StrategyAwardEntity> entityList = strategyAwards.stream()
                .map(strategyAward -> BeanUtil.copyProperties(strategyAward, StrategyAwardEntity.class))
                .collect(Collectors.toList());

        // 6.缓存JSON数据
        redisService.setList(key, entityList);
        return entityList;
    }

    @Override
    public void storeStrategyAwardSearchRateTables(String key, Integer rateRange, HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTables) {
        // 1.存储抽奖策略单位数量，用于生成范围以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);

        // 2.存储概率查找表
        RMap<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffledStrategyAwardSearchRateTables);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        if (!redisService.isExists(cacheKey)) {
            throw new AppException(UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), cacheKey + Constants.COLON + UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return redisService.getValue(cacheKey);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 1.从缓存获取
        String key = Constants.RedisKey.STRATEGY_KEY + strategyId;
        String json = redisService.getValue(key);
        // 2.判空
        if (null != json) {
            return JSON.parseObject(json, StrategyEntity.class);
        }
        // 3.查数据库
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        StrategyEntity strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();

        // 4.缓存数据
        redisService.setValue(key, JSON.toJSONString(strategyEntity));
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        StrategyRule query = new StrategyRule();
        query.setStrategyId(strategyId);
        query.setRuleModel(ruleModel);

        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(query);
        return BeanUtil.copyProperties(strategyRule, StrategyRuleEntity.class);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId, null, ruleModel);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule query = new StrategyRule();
        query.setStrategyId(strategyId);
        query.setAwardId(awardId);
        query.setRuleModel(ruleModel);
        return strategyRuleDao.queryStrategyRuleValue(query);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward query = new StrategyAward();
        query.setStrategyId(strategyId);
        query.setAwardId(awardId);
        String ruleModels = strategyAwardDao.queryStrategyAwardRuleModels(query);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();
    }

    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {

        // 查询缓存
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        String json = redisService.getValue(cacheKey);
        if (json != null && !json.isEmpty()) {
            return JSON.parseObject(json, RuleTreeVO.class);
        }

        RuleTree ruleTree = ruleTreeDao.queryRuleTreeTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        // 1.tree node line 转换Map结构
        Map<String, List<RuleTreeNodeLineVO>> treeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine line : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(line.getTreeId())
                    .ruleNodeFrom(line.getRuleNodeFrom())
                    .ruleNodeTo(line.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(line.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(line.getRuleLimitValue()))
                    .build();

            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = treeNodeLineMap.computeIfAbsent(line.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        // 2.tree node 转换Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode node : ruleTreeNodes) {
            RuleTreeNodeVO treeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(node.getTreeId())
                    .ruleKey(node.getRuleKey())
                    .ruleDesc(node.getRuleDesc())
                    .ruleValue(node.getRuleValue())
                    .nodeLineVOList(treeNodeLineMap.get(node.getRuleKey()))
                    .build();

            treeNodeMap.put(treeNodeVO.getRuleKey(), treeNodeVO);
        }

        // 3.构建 Rule Tree
        RuleTreeVO ruleTreeVO = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootNode(ruleTree.getTreeRootRuleKey())
                .treeNodeMap(treeNodeMap)
                .build();

        // 缓存
        redisService.setValue(Constants.RedisKey.RULE_TREE_VO_KEY + treeId, JSON.toJSONString(ruleTreeVO));
        return ruleTreeVO;
    }

    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        // 避免重复缓存
        if (redisService.getValue(cacheKey) != null) {
            return;
        }
        redisService.setAtomicLong(cacheKey, awardCount);
    }

    @Override
    public Boolean subtractionAwardStock(String cacheKey) {
        long surplus = redisService.decr(cacheKey);
        if (surplus < 0) {
            redisService.setValue(cacheKey, 0L);
            return false;
        }
        String lockKey = cacheKey + Constants.COLON + surplus;
        Boolean isLocked = redisService.setNx(lockKey);
        if (!isLocked) {
            log.warn("奖品库存加锁失败{}", lockKey);
        }
        return isLocked;
    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO stockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(stockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUEUE_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAwardDao.updateStrategyAwardStock(strategyAward);
    }

    /**
     * 根据策略ID+奖品ID的唯一组合，查询奖品信息
     *
     * @param strategyId
     * @param awardId
     * @return
     */
    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId) {

        // 缓存获取
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId + Constants.COLON + awardId;
        String json = redisService.getValue(cacheKey);
        if (json != null) {
            return JSON.parseObject(json, StrategyAwardEntity.class);
        }

        StrategyAward query = new StrategyAward();
        query.setStrategyId(strategyId);
        query.setAwardId(awardId);
        StrategyAward strategyAward = strategyAwardDao.queryStrategyAward(query);

        // 缓存
        StrategyAwardEntity strategyAwardEntity = BeanUtil.copyProperties(strategyAward, StrategyAwardEntity.class);
        redisService.setValue(cacheKey, JSON.toJSONString(strategyAwardEntity));
        return strategyAwardEntity;
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return raffleActivityDao.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public Integer queryTodayUserRaffleCount(String userId, Long strategyId) {
        // 活动ID
        Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        // 封装参数
        RaffleActivityAccountDay raffleActivityAccountDayReq = RaffleActivityAccountDay.builder()
                .userId(userId)
                .activityId(activityId)
                .day(RaffleActivityAccountDay.currentDay())
                .build();
        // 查询用户当日抽奖次数
        RaffleActivityAccountDay raffleActivityAccountDay
                = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if (raffleActivityAccountDay == null) return 0;
        // 总次数 - 剩余 = 今日使用的
        return raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }
}
