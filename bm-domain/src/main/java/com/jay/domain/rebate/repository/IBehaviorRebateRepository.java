package com.jay.domain.rebate.repository;


import com.jay.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.jay.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.jay.domain.rebate.model.vo.BehaviorTypeVO;
import com.jay.domain.rebate.model.vo.DailyBehaviorRebateVO;

import java.util.List;

/**
 * @author Jay
 * @date 2025/8/5 14:58
 * @description 行为返利服务仓储接口
 */
public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);

    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}
