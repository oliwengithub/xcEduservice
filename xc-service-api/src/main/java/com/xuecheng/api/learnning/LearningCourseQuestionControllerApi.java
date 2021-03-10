package com.xuecheng.api.learnning;

import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.domain.learning.response.LearningCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "课程讨论问答接口")
public interface LearningCourseQuestionControllerApi {

    @ApiOperation("获取讨论问答列表")
    public QueryResponseResult getCourseQuestionList (int page, int size);

    @ApiOperation("回复问题，提问接口")
    public ResponseResult applyQuestion(XcLearningCourseQuestion xcLearningCourseQuestion);
}
