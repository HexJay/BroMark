package com.jay.api;

import com.jay.api.dto.RaffleAwardListRequestDTO;
import com.jay.api.dto.RaffleAwardListResponseDTO;
import com.jay.api.dto.RaffleStrategyRequestDTO;
import com.jay.api.dto.RaffleStrategyResponseDTO;
import com.jay.api.response.Response;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/9 22:19
 * @description 抽奖服务接口
 */
public interface IRaffleService {

    /**
     * 策略装配接口
     *
     * @param strategyId 策略ID
     * @return 装配及结果
     */
    Response<Boolean> strategyArmory(Long strategyId);
    /**
     * 奖品列表接口
     * @param requestDTO 请求参数
     * @return 奖品列表
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);
    /**
     * 抽奖接口
     * @param requestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleStrategyResponseDTO> raffle(RaffleStrategyRequestDTO requestDTO);
}
