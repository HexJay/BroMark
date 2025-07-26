package com.jay.domain.task.repository;


import com.jay.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/26 10:04
 * @description TODO
 */
public interface ITaskRepository {

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFailed(String userId, String messageId);

    List<TaskEntity> queryNoSendMessageTaskList();
}
