package com.xuecheng.api.learnning;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "学生课程学习管理")
public interface ChooseCourseControllerApi {

    @ApiOperation("获取学生选课列")
    public QueryResponseResult getChooseCourseList (int page, int size, ChooseCourseResultList chooseCourseResultList);
}
