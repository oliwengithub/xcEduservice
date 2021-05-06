package com.xuecheng.learning.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.XcLearningCourseSchedule;
import com.xuecheng.framework.domain.learning.ext.CourseScheduleExt;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.learning.response.LearningCourseResult;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseClient;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.dao.LearningCourseMapper;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcLearningCourseScheduleRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

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
    private XcLearningCourseScheduleRepository xcLearningCourseScheduleRepository;

    @Resource
    private LearningCourseMapper learningCourseMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CourseClient courseClient;



    public GetMediaResult getMedia (String userId, String courseId, String teachplanId) {

        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(courseId) || StringUtils.isEmpty(teachplanId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 校验学生权限 ....
        // 1.获取学生选课信息
        XcLearningCourse learningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (learningCourse == null) {
            // 用户没有选课
            ExceptionCast.cast(LearningCode.CHOOSECOUSER_NOHAVE);
        }

        // 2.校验课程有效期
        if (checkCourseFlag(learningCourse.getValid(), learningCourse.getEndTime())) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_TASKISNULL);
        }
        // 远程调用获取课程计划对应的媒资地址
        TeachplanMediaPub media = courseSearchClient.getMedia(teachplanId);
        if (media == null || StringUtils.isEmpty(media.getMediaUrl())) {
            // 获取视频播放地址出错
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }

        Teachplan courseTeachplan = courseClient.getCourseTeachplan(teachplanId);
        if (courseTeachplan == null) {
            // 未获取到章节同样返回获取地址出错
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }

        // 构建课程进度更新对象
        CourseScheduleExt courseScheduleExt = new CourseScheduleExt();
        courseScheduleExt.setCourseId(courseId);
        courseScheduleExt.setTeachplanId(teachplanId);
        courseScheduleExt.setUserId(userId);
        courseScheduleExt.setTeachplanName(courseTeachplan.getPname());

        String jsonString = JSON.toJSONString(courseScheduleExt);
        // 获取课程地址成功 更新课程进度
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_SCHEDULE, RabbitMQConfig.XC_LEARNING_SCHEDULE_KEY, jsonString);

        return new GetMediaResult(CommonCode.SUCCESS, media.getMediaUrl());
    }


    /**
     * 校验课程的有效性
     * @author: olw
     * @Date: 2021/3/21 14:52
     * @param valid
     * @param endTime
     * @returns: boolean
    */
    private boolean checkCourseFlag(String valid, Date endTime) {
        if("204002".equals(valid) && new Date().before(endTime)) {
            return true;
        }
        return false;
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
    public ResponseResult addCourse (String courseName, int teachpalnNum, String teachplanId, String teachplanName, String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask) {
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
        xcLearningCourse.setCourseName(courseName);
        xcLearningCourse.setTeachpalnNum(teachpalnNum);
        xcLearningCourse.setTeachplanId(teachplanId);
        xcLearningCourse.setTeachplanName(teachplanName);
        // 选课状态 501001正常 501002结束 501003取消 501004未选课
        xcLearningCourse.setStatus("501001");
        // 设置初始化进度
        xcLearningCourse.setCompletePercent(new BigDecimal("0.00"));
        xcLearningCourseRepository.save(xcLearningCourse);
        // 向历史任务表插入记录 不需要

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 免费课程选课业务
     * @author: olw
     * @Date: 2020/12/18 20:36
     * @param courseId
     * @param userId
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult addOpenCourse (String courseId, String userId) {
        // 检验是否已经选课
        XcLearningCourse learningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (learningCourse != null) {
            // 不为空表示已经选课
            return new ResponseResult(CommonCode.SUCCESS);
        }
        // 获取课程相关信息
        CourseMarket courseMarket = courseClient.getCourseMarketById(courseId);
        // 课程对应的收费规则
        String charge = courseMarket.getCharge();
        if (!"203001".equals(charge)) {
            ExceptionCast.cast(LearningCode.CHOOSECO_NOOPEN);
        }
        // 获取课程发布信息
        Map<String, CoursePub> all = courseSearchClient.getAll(courseId);
        CoursePub coursePub = all.get(courseId);
        if (coursePub == null) {
            // 为空直接返回选课结束或不存在
            ExceptionCast.cast(LearningCode.CHOOSECO_ISEXPIRE);
        }
        //构建选课对象
        XcLearningCourse xcLearningCourse = new XcLearningCourse();
        xcLearningCourse.setCourseId(courseId);
        xcLearningCourse.setCourseName(coursePub.getName());
        xcLearningCourse.setUserId(userId);
        // 选课状态 501001正常 501002结束 501003取消 501004未选课
        xcLearningCourse.setStatus("501001");
        xcLearningCourse.setValid(coursePub.getValid());
        xcLearningCourse.setStartTime(courseMarket.getStartTime());
        xcLearningCourse.setEndTime(courseMarket.getEndTime());
        // 获取课程课时
        String teachplan = coursePub.getTeachplan();
        TeachplanNode teachplanNode = JSON.parseObject(teachplan, TeachplanNode.class);
        String teachplanId = "";
        String teachplanName = "";
        List<TeachplanNode> children = teachplanNode.getChildren();
        int count = 0;
        for (TeachplanNode child : children) {
            int num = child.getChildren().size();
            count = count + num;
            if (StringUtils.isEmpty(teachplanId)) {
                teachplanId = child.getChildren().get(0).getId();
                teachplanName = child.getChildren().get(0).getPname();

            }
        }
        xcLearningCourse.setTeachplanId(teachplanId);
        xcLearningCourse.setTeachplanName(teachplanName);
        xcLearningCourse.setTeachpalnNum(count);
        BigDecimal bigDecimal = new BigDecimal("0.00");
        xcLearningCourse.setCompletePercent(bigDecimal);
        xcLearningCourseRepository.save(xcLearningCourse);

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

    /**
     * 更新用户课程学习进度
     * @author: olw
     * @Date: 2020/11/30 19:37
     * @param courseScheduleExt
     * @returns: void
    */
    @Transactional(rollbackFor = Exception.class)
    public void updateCourseSchedule (CourseScheduleExt courseScheduleExt) {
        String courseId = courseScheduleExt.getCourseId();
        String userId = courseScheduleExt.getUserId();
        String teachplanId = courseScheduleExt.getTeachplanId();

        // 更新学生选课信息
        XcLearningCourse learningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (learningCourse == null) {
            return;
        }
        learningCourse.setUpdateTime(new Date());
        learningCourse.setTeachplanId(teachplanId);
        learningCourse.setTeachplanName(courseScheduleExt.getTeachplanName());
        // 更新进度
        Integer teachpalnNum = learningCourse.getTeachpalnNum();
        int finishNum = 0;
        List<XcLearningCourseSchedule> learningCourseSchedules = xcLearningCourseScheduleRepository.findByUserIdAndCourseId(userId, courseId);
        if (learningCourseSchedules != null && learningCourseSchedules.size() > 0) {
            finishNum = finishNum + learningCourseSchedules.size();
        }
        // 是否已经观看
        XcLearningCourseSchedule xcLearningCourseSchedule = xcLearningCourseScheduleRepository.findByUserIdAndTeachplanId(userId, teachplanId);
        if (xcLearningCourseSchedule == null) {
            finishNum = finishNum + 1;

        }
        DecimalFormat of = new DecimalFormat("0.00");
        String s = of.format((float) ((finishNum*1.0)*100  / teachpalnNum));
        BigDecimal percent = new BigDecimal(s);
        learningCourse.setCompletePercent(percent);

        XcLearningCourseSchedule newSchedule = new XcLearningCourseSchedule();
        BeanUtils.copyProperties(courseScheduleExt, newSchedule);
        newSchedule.setTeachplanId(teachplanId);

        xcLearningCourseRepository.save(learningCourse);
        xcLearningCourseScheduleRepository.save(newSchedule);
    }

    /**
     * 更新所有选课信息
     * @author: olw
     * @Date: 2020/12/2 16:13
     * @param courseId  课程id
     * @param teachplan  课程计划
     * @param name  课程名称
     * @returns: void
    */
    public void updateChooseCourse (String teachplan, String courseId, String name) {
        // 设置分批次更新 每次更新1000条
        // pageable 从0开始
        int page = 0;
        int size = 100;
        // 获取课程章节数量

        int count = getCourseTeachplanNum(teachplan);

        XcLearningCourse learningCourse = new XcLearningCourse();
        learningCourse.setCourseId(courseId);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("courseId", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<XcLearningCourse> example = Example.of(learningCourse, exampleMatcher);
        synchronized (this) {
            while (true){

                QueryResult<XcLearningCourse> queryResult = getLearningCourse(page, size, example);
                List<XcLearningCourse> list = queryResult.getList();
                // 这个是页码
                long total = queryResult.getTotal();
                if (total - page > 0) {
                    page = page +1;
                    // 只负责更新课程计划和名称，不更新学习进度
                    List<XcLearningCourse> collect = list.stream().peek(s -> {s.setTeachpalnNum(count);s.setCourseName(name);}).collect(Collectors.toList());
                    xcLearningCourseRepository.saveAll(collect);
                }else {
                    break;
                }
            }
        }


    }

    private QueryResult<XcLearningCourse> getLearningCourse (int page, int size, Example example) {
        Pageable pageable = new PageRequest(page, size);
        org.springframework.data.domain.Page all = xcLearningCourseRepository.findAll(example, pageable);
        QueryResult<XcLearningCourse> queryResult = new QueryResult<>();
        // 这个设置返回的时总页数
        queryResult.setTotal(all.getTotalPages());
        queryResult.setList(all.getContent());
        return queryResult;
    }

    /**
     * 获取课程章节
     * @author: olw
     * @Date: 2020/12/2 17:50
     * @param teachplan
     * @returns: int
    */
    private int getCourseTeachplanNum (String teachplan) {
        TeachplanNode teachplanNode = JSON.parseObject(teachplan, TeachplanNode.class);
        List<TeachplanNode> children = teachplanNode.getChildren();
        int count = 0;
        for (TeachplanNode child : children) {
            int num = child.getChildren().size();
            count = count + num;
        }
        return count;

    }

    /**
     * 根据用户id和课程id获取用户的选课状态
     * @author: olw
     * @Date: 2020/12/13 14:16
     * @param courseId
     * @param userId
     * @returns: com.xuecheng.framework.domain.learning.response.LearningCourseResult
    */
    public LearningCourseResult queryLearnStatus (String courseId, String userId) {
        if (StringUtils.isEmpty(courseId) || StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        XcLearningCourse xcLearningCourse = xcLearningCourseRepository.findByUserIdAndCourseId(userId, courseId);
        return new LearningCourseResult(CommonCode.SUCCESS, xcLearningCourse);
    }
}
