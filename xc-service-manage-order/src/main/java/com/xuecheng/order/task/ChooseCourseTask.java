package com.xuecheng.order.task;

import com.sun.xml.internal.bind.v2.TODO;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.dao.XcTaskRepository;
import com.xuecheng.order.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author: olw
 * @date: 2020/10/24 13:17
 * @description:  选课任务测试
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Resource
    private TaskService taskService;

    /**
     * 发送添加选课消息
     * @author: olw
     * @Date: 2020/10/24 16:27
     * @returns: void
    */
    @Scheduled(fixedRate = 1000)
    public void sendChooseCourseTask () {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        // TODO: 2020/10/25 方便测试直修改时间间隔为5秒
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)-5);
        Date time = calendar.getTime();

        List<XcTask> xcTaskList = taskService.findXcTaskList(time, 100);
        if (xcTaskList != null && xcTaskList.size() > 0) {
            for (XcTask xcTask : xcTaskList) {
                // 通过更新版本号来解决消息被多次发送的问题（乐观锁）
                int i = taskService.updateTaskVersion(xcTask.getId(), xcTask.getVersion());
                if (i>0) {
                    try {
                        LOGGER.info("send choose course task,taskId:{}",xcTask.getId());
                        taskService.sendChooseCourseMsg(xcTask.getMqExchange(), xcTask.getMqRoutingkey(), xcTask);
                    }catch (Exception e) {
                        LOGGER.error("send choose course task,taskId:{}",xcTask.getId());
                    }
                }
            }
        }
    }


    //@Scheduled(fixedDelay = 4000)
    public void task () throws InterruptedException {
        System.out.println("fixedDelay:" + new Date());
        Thread.sleep(5000);
    }

    //@Scheduled(fixedRate = 1000)
    public void task1() throws InterruptedException {
        System.out.println("fixedRate:" + new Date());
        Thread.sleep(2000);
    }





}
