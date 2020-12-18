package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.XcLearningCourseState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcLearningCourseStateRepository extends JpaRepository<XcLearningCourseState, String> {


}
