package com.jay.trigger.job;


import com.jay.domain.activity.ISkuStock;
import com.jay.domain.activity.model.vo.ActivitySkuStockKeyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/7/20 23:43
 * @description 更新sku库存任务
 */
@Slf4j
@Component
public class UpdateActivitySkuStockJob {

    @Resource
    private ISkuStock skuStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void execute() {
        try {
            log.info("定时任务，更新活动sku库存");
            ActivitySkuStockKeyVO activitySkuStockKeyVO = skuStock.takeQueueValue();
            if (activitySkuStockKeyVO == null) return;
            log.info("定时任务，更新活动sku库存 sku:{} activityId:{}", activitySkuStockKeyVO.getSku(), activitySkuStockKeyVO.getActivityId());
            skuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
        } catch (Exception e) {
            log.error("定时任务，更新活动sku库存失败", e);
        }
    }
}
