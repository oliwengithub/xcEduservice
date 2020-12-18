package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class LearningCourseResult extends ResponseResult {
    XcLearningCourse xcLearningCourse;

    public LearningCourseResult (ResultCode resultCode, XcLearningCourse xcLearningCourse) {
        super(resultCode);
        this.xcLearningCourse = xcLearningCourse;
    }
}
