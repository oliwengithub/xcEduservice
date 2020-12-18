package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningCourseQuestionRepository extends JpaRepository<XcLearningCourseQuestion, String> {

}
