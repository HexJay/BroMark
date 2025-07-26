package com.jay.infrastructure.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.jay.infrastructure.dao.po.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jay
 * @date 2025/7/22 21:02
 * @description 任务表，发送MQ
 */
@Mapper
public interface ITaskDao {
    void insert(Task task);
    @DBRouter
    void updateTaskSendMessageCompleted(Task task);
    @DBRouter
    void updateTaskSendMessageFailed(Task taskReq);

    List<Task> queryNoSendMessageTaskList();
}
