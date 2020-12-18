package com.xuecheng.api.learnning;

import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.domain.learning.response.LearningCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "学生课程学习管理")
public interface LearningCourseQuestionControllerApi {

    @ApiOperation("获取学生选课列")
    public QueryResponseResult getChooseCourseList (int page, int size, ChooseCourseResultList chooseCourseResultList);

    @ApiOperation("免费报名课程接口")
    public ResponseResult addOpenCourse (String courseId);

    @ApiOperation("查询学生选课状态接口")
    public LearningCourseResult queryLearnStatus (String courseId);
}
