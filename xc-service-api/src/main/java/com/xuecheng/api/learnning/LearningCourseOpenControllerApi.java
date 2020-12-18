package com.xuecheng.api.learnning;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import io.swagger.annotations.Api;

@Api(value = "课程开放接口")
public interface LearningCourseOpenControllerApi {

    public XcLearningCourse getLearningCourse ();
}
