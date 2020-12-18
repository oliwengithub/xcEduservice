package com.xuecheng.framework.domain.learning.ext;

import com.xuecheng.framework.domain.learning.XcLearningCourseSchedule;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseScheduleExt extends XcLearningCourseSchedule {

    private String courseName;
    private String teachplanName;

}
