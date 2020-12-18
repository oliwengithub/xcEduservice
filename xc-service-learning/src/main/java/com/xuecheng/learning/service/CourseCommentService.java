package com.xuecheng.learning.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.requset.CourseCommentResultList;
import com.xuecheng.framework.domain.learning.response.CourseCommentDetail;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseClient;
import com.xuecheng.learning.client.UserClient;
import com.xuecheng.learning.dao.CourseCommentMapper;
import com.xuecheng.learning.dao.XcLearningCourseCommentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author: olw
 * @date: 2020/12/13 15:58
 * @description:  课程评论业务
 */
@Service
public class CourseCommentService {

    @Resource
    CourseCommentMapper courseCommentMapper;

    @Autowired
    XcLearningCourseCommentRepository xcLearningCourseCommentRepository;

    @Autowired
    UserClient userClient;

    @Autowired
    CourseClient courseClient;


    /**
     * 获取所有品论列表
     * @author: olw
     * @Date: 2020/12/13 16:02
     * @param page
     * @param size
     * @param courseCommentResultList
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
    */
    public QueryResponseResult getCourseCommentList (int page, int size, CourseCommentResultList courseCommentResultList) {
        if (courseCommentResultList == null) {
            courseCommentResultList = new CourseCommentResultList();
        }
        page = page <= 0 ? 1 : page;
        size = size <= 0 ? 10 : size;

        PageHelper.startPage(page, size);
        Page<XcLearningCourseComment> courseCommentList = courseCommentMapper.getCourseCommentList(courseCommentResultList);
        QueryResult<XcLearningCourseComment> queryResult = new QueryResult<>();
        queryResult.setList(courseCommentList.getResult());
        queryResult.setTotal(courseCommentList.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }


    public ResponseResult add (XcLearningCourseComment xcLearningCourseComment) {
        xcLearningCourseComment.setCreateTime(new Date());
        xcLearningCourseComment.setStatus("205001");
        xcLearningCourseCommentRepository.save(xcLearningCourseComment);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 审核评论
     * @author: olw
     * @Date: 2020/12/13 16:46
     * @param xcLearningCourseComment
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult checkCourseComment (XcLearningCourseComment xcLearningCourseComment) {
        String id = xcLearningCourseComment.getId();
        if (StringUtils.isEmpty(id)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<XcLearningCourseComment> optional = xcLearningCourseCommentRepository.findById(id);
        if(optional.isPresent()) {
            XcLearningCourseComment courseComment = optional.get();
            courseComment.setStatus(xcLearningCourseComment.getStatus());
            xcLearningCourseCommentRepository.save(courseComment);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 获取课程评论详情
     * @author: olw
     * @Date: 2020/12/13 17:26
     * @param commentId
     * @returns: com.xuecheng.framework.domain.learning.response.CourseCommentDetail
    */
    public CourseCommentDetail commentDetail (String commentId) {
        CourseCommentDetail courseCommentDetail = new CourseCommentDetail();
        Optional<XcLearningCourseComment> optional = xcLearningCourseCommentRepository.findById(commentId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(LearningCode.COURSE_COMMENT_ISNULL);
        }
        XcLearningCourseComment courseComment = optional.get();
        courseCommentDetail.setXcLearningCourseComment(courseComment);
        // 获取用户信息
        XcUser user = userClient.getUser(courseComment.getUserId());
        if (user != null) {
            courseCommentDetail.setXcUser(user);
        }
        // 获取课程信息
        CourseBase courseBase = courseClient.getCourseBaseById(courseComment.getCourseId());
        if (courseBase != null) {
            courseCommentDetail.setCourseBase(courseBase);
        }
        return courseCommentDetail;
    }
}
