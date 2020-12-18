package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningCourseCommentRepository extends JpaRepository<XcLearningCourseComment, String> {

}
