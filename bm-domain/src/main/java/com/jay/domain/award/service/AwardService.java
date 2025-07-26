package com.jay.domain.award.service;


import com.jay.domain.award.event.SendAwardMessageEvent;
import com.jay.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.jay.domain.award.model.entity.TaskEntity;
import com.jay.domain.award.model.entity.UserAwardRecordEntity;
import com.jay.domain.award.model.vo.TaskStateVO;
import com.jay.domain.award.repository.IAwardRepository;
import com.jay.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jay
 * @date 2025/7/26 00:18
 * @description TODO
 */
@Service
public class AwardService implements IAwardService {

    @Resource
    private IAwardRepository repository;
    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        // 1.构建消息实体对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = new SendAwardMessageEvent.SendAwardMessage();
        sendAwardMessage.setUserId(userAwardRecordEntity.getUserId());
        sendAwardMessage.setAwardId(userAwardRecordEntity.getAwardId());
        sendAwardMessage.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage
                = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        // 2.构建任务对象
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .messageId(sendAwardMessageEventMessage.getId())
                .message(sendAwardMessageEventMessage)
                .state(TaskStateVO.create)
                .build();

        // 3.构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();

        // 4.存储聚合对象
         repository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
