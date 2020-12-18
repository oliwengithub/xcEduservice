package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.ucenter.XcUser;
import lombok.Data;
import lombok.ToString;

/**
 * @author MaiBenBen
 */
@Data
@ToString
public class CourseCommentDetail {
    private CourseBase courseBase;
    private XcUser xcUser;
    private XcLearningCourseComment xcLearningCourseComment;
}
