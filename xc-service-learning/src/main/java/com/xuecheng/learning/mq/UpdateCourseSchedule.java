package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.learning.ext.CourseScheduleExt;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author: olw
 * @date: 2020/11/30 17:11
 * @description:  更新课程学习进度
 */
@Component
public class UpdateCourseSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCourseSchedule.class);

    @Autowired
    LearningService learningService;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_SCHEDULE)
    public void UpdateSchedule(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        Map map = JSON.parseObject(msg, Map.class);
        String teachplanId = (String) map.get("teachplanId");
        if (StringUtils.isEmpty(teachplanId)) {
            String courseId = (String) map.get("courseId");
            String teachplan = (String) map.get("teachplan");
            String name = (String) map.get("name");
            // 发布新课程
            learningService.updateChooseCourse(teachplan, courseId, name);
            return;
        }
        CourseScheduleExt courseScheduleExt = JSON.parseObject(msg, CourseScheduleExt.class);
        courseScheduleExt.setUpdateTime(new Date());
        try {
            learningService.updateCourseSchedule(courseScheduleExt);
        }catch (Exception e) {
            LOGGER.error("更新课程学习进度失败 e:{}, msg:{}", e, msg);
        }
    }
}
