package com.xuecheng.learning.task;

import com.xuecheng.learning.service.CourseStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * @author: olw
 * @date: 2020/12/16 17:10
 * @description:  更新课程统计信息定时任务
 */


@Component
public class CourseStateTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseStateTask.class);

    @Autowired
    CourseStateService courseStateService;

    //@Scheduled(fixedRate =  1000)
    public void CourseStateTask () {
        try {
            LOGGER.info("课程统计任务开始"+ new Date());
            courseStateService.updateCourseState();
            LOGGER.info("课程统计任务结束"+ new Date());
        }catch (Exception e) {
            LOGGER.error("更新课程统计信息失败", e);
        }

    }
}
