package com.jay.domain.award.service.distribute;


import com.jay.domain.award.model.entity.DistributeAwardEntity;

/**
 * @author Jay
 * @date 2025/8/6 18:06
 * @description 分发奖品接口
 */
public interface IDistributeAward {
    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
}
