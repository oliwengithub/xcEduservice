package com.xuecheng.learning.controller;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.api.learnning.LearningCourseCommentControllerApi;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.requset.CourseCommentResultList;
import com.xuecheng.framework.domain.learning.response.CourseCommentDetail;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.learning.service.CourseCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author: olw
 * @date: 2020/12/13 15:52
 * @description:  课程评论模块
 */
@RestController
@RequestMapping("/learning/comment")
public class LearningCourseCommentController extends BaseController implements LearningCourseCommentControllerApi {

    @Autowired
    CourseCommentService courseCommentService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult getCourseCommentList (@PathVariable("page") int page, @PathVariable("size") int size, CourseCommentResultList courseCommentResultList) {
        return courseCommentService.getCourseCommentList(page, size, courseCommentResultList);
    }

    @Override
    @PostMapping("/add")
    public ResponseResult addCourseComment (@RequestBody XcLearningCourseComment xcLearningCourseComment) {
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt == null) {
            ExceptionCast.cast(CommonCode.UNAUTHENTICATED);
        }
        xcLearningCourseComment.setUserId(userJwt.getId());
        return courseCommentService.add(xcLearningCourseComment);
    }

    @Override
    @PutMapping("/check")
    public ResponseResult checkCourseComment (@RequestBody XcLearningCourseComment xcLearningCourseComment) {
        return courseCommentService.checkCourseComment(xcLearningCourseComment);
    }

    @Override
    public CourseCommentDetail CourseCommentDetail (String commentId) {
        return courseCommentService.commentDetail(commentId);
    }
}
