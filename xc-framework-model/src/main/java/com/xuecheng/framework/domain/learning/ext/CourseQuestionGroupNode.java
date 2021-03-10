package com.xuecheng.framework.domain.learning.ext;

import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CourseQuestionGroupNode extends XcLearningCourseQuestion {

    private List<CourseQuestionExt> questionExts;

    /**
     * 提问userId
    */
    private String applyUserId;

    private Boolean inputShow = true;


}
