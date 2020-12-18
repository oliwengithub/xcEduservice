package com.xuecheng.api.learnning;

import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.domain.learning.requset.CourseCommentResultList;
import com.xuecheng.framework.domain.learning.response.CourseCommentDetail;
import com.xuecheng.framework.domain.learning.response.LearningCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程评论接口")
public interface LearningCourseCommentControllerApi {

    @ApiOperation("获取课程评论列表")
    public QueryResponseResult getCourseCommentList (int page, int size, CourseCommentResultList courseCommentResultList);

    @ApiOperation("添加评论接口")
    public ResponseResult addCourseComment (XcLearningCourseComment xcLearningCourseComment);

    @ApiOperation("审核评论接口")
    public ResponseResult checkCourseComment (XcLearningCourseComment xcLearningCourseComment);

    @ApiOperation("审核评论接口")
    public CourseCommentDetail CourseCommentDetail (String commentId);



}
