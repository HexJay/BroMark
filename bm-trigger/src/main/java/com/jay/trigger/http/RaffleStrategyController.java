package com.jay.trigger.http;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.jay.api.IRaffleService;
import com.jay.api.dto.RaffleAwardListRequestDTO;
import com.jay.api.dto.RaffleAwardListResponseDTO;
import com.jay.api.dto.RaffleStrategyRequestDTO;
import com.jay.api.dto.RaffleStrategyResponseDTO;
import com.jay.api.response.Response;
import com.jay.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.jay.domain.strategy.model.entity.RaffleAwardEntity;
import com.jay.domain.strategy.model.entity.RaffleFactorEntity;
import com.jay.domain.strategy.model.entity.StrategyAwardEntity;
import com.jay.domain.strategy.service.IRaffleAward;
import com.jay.domain.strategy.service.IRaffleRule;
import com.jay.domain.strategy.service.IRaffleStrategy;
import com.jay.domain.strategy.service.armory.IStrategyArmory;
import com.jay.types.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jay
 * @date 2025/7/10 16:06
 * @description 抽奖服务
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
public class RaffleStrategyController implements IRaffleService {

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleAward raffleAWard;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private IRaffleRule raffleRule;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    /**
     * 策略装配，将策略信息装配到缓存中
     * <a href="http://localhost:8091/api/v1/raffle/strategy_armory">/api/v1/raffle/strategy_armory</a>
     *
     * @param strategyId 策略ID
     * @return 装配结果
     */
    @Override
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    public Response<Boolean> strategyArmory(Long strategyId) {
        try {
            log.info("抽奖策略装配开始 strategyId: {}", strategyId);
            strategyArmory.assembleLotteryStrategy(strategyId);
            log.info("抽奖策略装配完成 strategyId: {}", strategyId);
            return Response.ok();
        } catch (Exception e) {
            log.error("抽奖策略装配失败 strategyId: {}", strategyId, e);
            // return Response.<Boolean>builder()
            //         .code(ResponseCode.UN_ERROR.getCode())
            //         .info(ResponseCode.UN_ERROR.getInfo())
            //         .data(false)
            //         .build();
            return Response.fail(ResponseCode.UN_ERROR, null);
        }
    }

    /**
     * 查询奖品列表
     * <a href="http://localhost:8091/api/v1/raffle/query_raffle_award_list">/api/v1/raffle/query_raffle_award_list</a>
     * 请求参数 raw json
     *
     * @param request {"strategyId":1000001}
     * @return 奖品列表
     */
    @Override
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO request) {
        try {
            log.info("查询奖品列表配置 userId: {}, activityId:{}", request.getUserId(), request.getActivityId());
            // 1.参数校验
            if (StringUtils.isBlank(request.getUserId()) || null == request.getActivityId()) {
                return Response.fail(ResponseCode.ILLEGAL_PARAMETER, null);
            }
            // 2. 奖品信息查询
            List<StrategyAwardEntity> strategyAwardEntities = raffleAWard.queryRaffleStrategyAwardsByActivityId(request.getActivityId());
            // 3. 获取规则配置
            String[] treeIds = strategyAwardEntities.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(ruleModel -> ruleModel != null && !ruleModel.isEmpty())
                    .toArray(String[]::new);
            // 4.查询规则配置 - 获取奖品解锁限制
            Map<String, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(treeIds);
            // 5.查询抽奖次数 - 用户已经参与抽奖的次数
            Integer dayPartakeCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeCount(request.getUserId(), request.getActivityId());
            // 6.填充数据
            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOS =
                    strategyAwardEntities.stream()
                            .map(entity -> {
                                Integer awardRuleLockCount = ruleLockCountMap.get(entity.getRuleModels());
                                RaffleAwardListResponseDTO responseDTO = BeanUtil.copyProperties(entity, RaffleAwardListResponseDTO.class);
                                responseDTO.setAwardLockCount(awardRuleLockCount);
                                responseDTO.setIsAwardUnlock(null == awardRuleLockCount || dayPartakeCount > awardRuleLockCount);
                                responseDTO.setWaitUnlockCount(null == awardRuleLockCount || awardRuleLockCount <= dayPartakeCount ? 0 : awardRuleLockCount - dayPartakeCount);
                                return responseDTO;
                            })
                            .collect(Collectors.toList());
            return Response.ok(raffleAwardListResponseDTOS);
        } catch (Exception e) {
            log.error("查询奖品列表配置 userId: {}, activityId:{}", request.getUserId(), request.getActivityId());
            return Response.fail(ResponseCode.UN_ERROR, null);
        }
    }

    /**
     * 随机抽奖接口
     * <a href="http://localhost:8091/api/v1/raffle/random_raffle">/api/v1/raffle/random_raffle</a>
     *
     * @param request 请求参数 {"strategyId":1000001}
     * @return 抽奖结果
     */
    @Override
    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    public Response<RaffleStrategyResponseDTO> raffle(@RequestBody RaffleStrategyRequestDTO request) {
        try {
            log.info("随机抽奖开始 strategyId: {}", request.getStrategyId());
            // 调用抽奖接口
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(request.getStrategyId())
                    .build());
            // 封装返回结果
            RaffleStrategyResponseDTO response = RaffleStrategyResponseDTO.builder()
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardIndex(raffleAwardEntity.getSort())
                    .build();
            log.info("随机抽奖完成 strategyId: {} response: {}", request.getStrategyId(), JSON.toJSONString(response));
            return Response.ok(response);
        } catch (Exception e) {
            log.error("随机抽奖失败 strategyId：{}", request.getStrategyId(), e);
            return Response.fail(ResponseCode.UN_ERROR, null);
        }
    }

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public Response<?> hello(String id) {
        log.info("hello id: {}", id);
        return Response.ok("hello " + id);
    }
}
