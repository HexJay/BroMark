package com.jay.domain.award.repository;


import com.jay.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.jay.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * @author Jay
 * @date 2025/7/26 00:16
 * @description TODO
 */
public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);

    String queryAwardConfig(Integer awardId);

    String queryAwardKey(Integer awardId);
}
