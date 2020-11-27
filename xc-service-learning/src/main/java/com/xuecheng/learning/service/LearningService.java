package com.xuecheng.learning.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.LearningCourseMapper;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author: olw
 * @date: 2020/10/13 16:33
 * @description:  学习中心
 */
@Service
public class LearningService {

    @Autowired
    private CourseSearchClient courseSearchClient;

    @Resource
    private XcLearningCourseRepository xcLearningCourseRepository;

    @Resource
    private XcTaskHisRepository xcTaskHisRepository;

    @Resource
    private LearningCourseMapper learningCourseMapper;


    public GetMediaResult getMedia (String courseId, String teachplanId) {

        // 校验学生权限 ....

        // 远程调用获取课程计划对应的媒资地址
        TeachplanMediaPub media = courseSearchClient.getMedia(teachplanId);
        if (media == null || StringUtils.isEmpty(media.getMediaUrl())) {
            // 获取视频播放地址出错
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }

        return new GetMediaResult(CommonCode.SUCCESS, media.getMediaUrl());
    }


    /**
     * 添加学生选课
     * @author: olw
     * @Date: 2020/10/27 15:14
     * @param userId
     * @param courseId
     * @param valid
     * @param startTime
     * @param endTime
     * @param xcTask
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult addCourse (String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        if (StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULL);
        }
        if(xcTask == null || StringUtils.isEmpty(xcTask.getId())){
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }
        // 先校验是否已经选课
        XcLearningCourse xcLearningCourse = getUserChooseCourseByUserIdAndCourseId(userId, courseId);
        // 有选课记录则更新日期 没有选课记录则添加
        if (xcLearningCourse == null) {
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
        }

        xcLearningCourse.setValid(valid);
        xcLearningCourse.setStartTime(startTime);
        xcLearningCourse.setEndTime(endTime);
        // 选课状态 501001正常 501002结束 501003取消 501004未选课
        xcLearningCourse.setStatus("501001");
        xcLearningCourseRepository.save(xcLearningCourse);
        // 向历史任务表插入记录
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if(!optional.isPresent()) {
            // 添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据userId和课程courseId获取学生选课情况
     * @author: olw
     * @Date: 2020/10/25 12:23
     * @param userId
     * @param courseId
     * @returns: com.xuecheng.framework.domain.learning.XcLearningCourse
    */
    public XcLearningCourse getUserChooseCourseByUserIdAndCourseId (String userId, String courseId) {
        return xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
    }

    /**
     * 添加学生选课
     * @author: olw
     * @Date: 2020/10/27 14:51
     * @param userId
     * @param courseId
     * @returns: void
    */
    public void saveLearningCourse (String userId, String courseId) {
        XcLearningCourse xcLearningCourse = new XcLearningCourse();
        xcLearningCourse.setCourseId(userId);
        xcLearningCourse.setCourseId(courseId);
        // 选课状态 501001正常 501002结束 501003取消 501004未选课
        xcLearningCourse.setStatus("501001");
        // 课程有效性 204001永久有效 204002指时间段有效
        xcLearningCourse.setValid("204001");
        XcLearningCourse save = xcLearningCourseRepository.save(xcLearningCourse);
    }


    /**
     * 查询学生选课列表
     * @author: olw
     * @Date: 2020/11/2 15:39
     * @param page
     * @param size
     * @param chooseCourseResultList
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
    */
    public QueryResponseResult getChooseCourseList (int page, int size, ChooseCourseResultList chooseCourseResultList) {
        if(chooseCourseResultList == null) {
            chooseCourseResultList = new ChooseCourseResultList();
        }
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 5;
        }
        PageHelper.startPage(page, size);
        Page<XcLearningCourse> courseListPage = learningCourseMapper.findChooseCourseListPage(chooseCourseResultList);
        // 构建返回对象
        QueryResult<XcLearningCourse> queryResult = new QueryResult<>();
        long total = courseListPage.getTotal();
        queryResult.setList(courseListPage.getResult());
        queryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }
}
