package com.jay.domain.award.service;


import com.jay.domain.award.model.entity.DistributeAwardEntity;
import com.jay.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @author Jay
 * @date 2025/7/25 18:12
 * @description 奖品服务接口
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
     * 配送发货奖品
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}
