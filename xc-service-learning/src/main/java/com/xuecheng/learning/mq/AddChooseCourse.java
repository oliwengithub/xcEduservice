package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.service.LearningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author: olw
 * @date: 2020/10/25 11:23
 * @description:  添加选课消息
 */
@Component
public class AddChooseCourse {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddChooseCourse.class);

    @Resource
    private LearningService learningService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE})
    public void addCourse (String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        XcTask xcTask = JSON.parseObject(msg, XcTask.class);
        // 接收到的消息id
        String id = xcTask.getId();
        LOGGER.info("receive choose course task,taskId:{}",xcTask.getId());
        try {
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
            String valid = (String) map.get("valid");
            String courseName = (String) map.get("courseName");
            String teachpalnId = (String) map.get("teachpalnId");
            String teachpalnName = (String) map.get("teachpalnName");
            Integer teachpalnNum = (Integer) map.get("teachpalnNum");

            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
            if(map.get("startTime")!=null){
                startTime =dateFormat.parse((String) map.get("startTime"));
            }
            if(map.get("endTime")!=null){
                endTime =dateFormat.parse((String) map.get("endTime"));
            }
            // 添加选课
            ResponseResult addCourse = learningService.addCourse(courseName, teachpalnNum, teachpalnId, teachpalnName, userId, courseId, valid, startTime, endTime, xcTask);
            // 选课成功发送响应消息
            if(addCourse.isSuccess()){
                // 向mq发送选课消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE, RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, JSON.toJSONString(xcTask));
                LOGGER.info("send finish choose course taskId:{}",id);
            }
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("send finish choose course taskId:{}", id);
        }

    }
}
