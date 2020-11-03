package com.xuecheng.order.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author: olw
 * @date: 2020/10/25 10:58
 * @description:  接受完成选课消息
 */
@Component
public class FinishAddCourse {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinishAddCourse.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Resource
    TaskService taskService;


    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void finishAddCourse (String msg) {
        if (msg == null && StringUtils.isEmpty(msg)) {
            return;
        }
        XcTask xcTask = JSON.parseObject(msg, XcTask.class);
        // 接收到的消息id

        String id = xcTask.getId();
        LOGGER.info("receive finish choose course task,taskId:{}",xcTask.getId());
        try {
            // 删除任务，将任务保存到任务历史表
            taskService.finishChooseCourse(xcTask);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("receive finish choose course taskId:{}", id);
        }
    }
}
