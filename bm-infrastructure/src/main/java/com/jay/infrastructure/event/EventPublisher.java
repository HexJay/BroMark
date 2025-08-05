package com.jay.infrastructure.event;


import com.alibaba.fastjson.JSON;
import com.jay.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Jay
 * @date 2025/7/19 17:15
 * @description 消息发送
 */
@Slf4j
@Component
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(String topic, BaseEvent.EventMessage<?> eventMassage) {
        try {
            String messageJson = JSON.toJSONString(eventMassage);
            rabbitTemplate.convertAndSend(topic, messageJson);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJson);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, eventMassage);
            throw e;
        }
    }

    public void publish(String topic, String eventMassageJSON) {
        try {
            rabbitTemplate.convertAndSend(topic, eventMassageJSON);
            log.info("发送MQ消息 topic:{} message:{}", topic, eventMassageJSON);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, eventMassageJSON);
            throw e;
        }
    }
}
