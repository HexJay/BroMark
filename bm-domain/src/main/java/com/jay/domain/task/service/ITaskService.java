package com.jay.domain.task.service;


import com.jay.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/26 09:59
 * @description 消息任务服务接口
 */
public interface ITaskService {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFailed(String userId, String messageId);
}
