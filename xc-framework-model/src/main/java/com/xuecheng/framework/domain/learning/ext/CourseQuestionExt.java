package com.xuecheng.framework.domain.learning.ext;

import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseQuestionExt extends XcLearningCourseQuestion {

    /**
     * 提问userId
     */
    private String applyUserId;

    private Boolean inputShow = true;
}
