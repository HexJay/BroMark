package com.jay.domain.credit.model.entity;


import com.jay.domain.credit.event.CreditAdjustSuccessMessageEvent;
import com.jay.types.enums.TaskState;
import com.jay.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jay
 * @date 2025/8/5 15:16
 * @description 任务实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /**
     * 活动ID
     */
    private String userId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息编号
     */
    private String messageId;
    /**
     * 消息主体
     */
    private BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> message;
    /**
     * 任务状态；create-创建、completed-完成、fail-失败
     */
    private TaskState state;

}

