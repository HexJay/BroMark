package com.jay.api;

import com.jay.api.dto.*;
import com.jay.api.response.Response;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/9 22:19
 * @description 抽奖服务接口
 */
public interface IRaffleStrategyService {

    /**
     * 策略装配接口
     *
     * @param strategyId 策略ID
     * @return 装配及结果
     */
    Response<Boolean> strategyArmory(Long strategyId);
    /**
     * 奖品列表接口
     * @param request 请求参数
     * @return 奖品列表
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO request);
    /**
     * 抽奖接口
     * @param request 请求参数
     * @return 抽奖结果
     */
    Response<RaffleStrategyResponseDTO> raffle(RaffleStrategyRequestDTO request);
    /**
     * 查询抽奖策略权重规则，给用户展示出抽奖N次后必中奖奖品范围
     *
     * @param request 请求对象
     * @return 权重奖品配置列表「这里会返回全部，前端可按需展示一条已达标的，或者一条要达标的」
     */
    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO request);

}
