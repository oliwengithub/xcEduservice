package com.xuecheng.order.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author: olw
 * @date: 2020/10/24 15:14
 * @description:
 */
@Service
public class TaskService {

    @Resource
    private XcTaskRepository xcTaskRepository;

    @Resource
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 添加选课信息定时任务列表
     * @author: olw
     * @Date: 2020/10/24 16:17
     * @param dateTime
     * @param size
     * @returns: java.util.List<com.xuecheng.framework.domain.task.XcTask>
    */
    public List<XcTask> findXcTaskList (Date dateTime, int size) {
        Pageable pageable = new PageRequest(0, size);
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable, dateTime);
        List<XcTask> xcTasksList = xcTasks.getContent();
        return xcTasksList;
    }


    /**
     *  发送添加选课消息到MQ
     * @author: olw
     * @Date: 2020/10/24 17:06
     * @param exchange
     * @param routingKey
     * @param xcTask
     * @returns: void
    */
    public void sendChooseCourseMsg (String exchange, String routingKey, XcTask xcTask) {
        // 先查询任务表是否有数据
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()){
            String jsonString = JSON.toJSONString(xcTask);
            rabbitTemplate.convertAndSend(exchange, routingKey, jsonString);
            // 更新任务时间
            xcTask = optional.get();
            xcTask.setUpdateTime(new Date());
            xcTaskRepository.save(xcTask);
        }
    }

    /**
     * 更新版本号
     * @author: olw
     * @Date: 2020/10/25 10:50
     * @param id
     * @param version
     * @returns: int
    */
    @Transactional(rollbackFor = Exception.class)
    public int updateTaskVersion (String id, int version) {
        return xcTaskRepository.updateVersion(id, version);
    }


    /**
     * 完成添加选课
     * @author: olw
     * @Date: 2020/10/27 16:14
     * @param xcTask
     * @returns: void
    */
    @Transactional(rollbackFor = Exception.class)
    public void finishChooseCourse (XcTask xcTask) {
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if(taskOptional.isPresent()){
            xcTask = taskOptional.get();
            xcTask.setDeleteTime(new Date());
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            // 105001未完成 105002 完成 105003 错误
            xcTaskHis.setStatus("105002");
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }

    }
}
