package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import org.hibernate.validator.constraints.EAN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcLearningCourseCommentRepository extends JpaRepository<XcLearningCourseComment, String> {

    public List<XcLearningCourseComment> findByCourseIdAndStatus(String courseId, String status);

}
