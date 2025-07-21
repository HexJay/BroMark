package com.jay.trigger.listener;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.jay.domain.activity.ISkuStock;
import com.jay.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/7/20 23:48
 * @description 活动sku库存耗尽
 */
@Slf4j
@Component
public class ActivitySkuStockZeroCustomer {

    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;

    @Resource
    private ISkuStock skuStock;

    /**
     * 监听topic为activity_sku_stock_zero
     * @param message 监听到的消息
     */
    @RabbitListener(queuesToDeclare = @Queue(value = "activity_sku_stock_zero"))
    public void listener(String message){
        try{
            log.info("监听活动sku库存消耗为 0 消息 topic:{} message:{}", "activity_sku_stock_zero", message);
            //转换对象
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<Long>>() {
            }.getType());
            Long sku = eventMessage.getData();
            //更新库存
            skuStock.clearActivitySkuStock(sku);
            //清空队列
            skuStock.clearQueue();
        }catch (Exception e){
            log.error("监听活动sku库存消耗为0消息，消费失败 topic:{} message:{}", topic, message);
            throw e;
        }
    }
}
