package com.jay.domain.award.model.entity;


import com.jay.domain.award.event.SendAwardMessageEvent;
import com.jay.domain.award.model.vo.TaskStateVO;
import com.jay.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/7/25 18:17
 * @description TODO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    /** 用户ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息ID */
    private String messageId;
    /** 消息主体 */
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private TaskStateVO state;
}
