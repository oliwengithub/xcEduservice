package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.learning.XcLearningCourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface XcLearningCourseScheduleRepository extends JpaRepository<XcLearningCourseSchedule, String> {

    public List<XcLearningCourseSchedule> findByUserIdAndCourseId (String userId, String courseId);

}
