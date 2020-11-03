package com.xuecheng.learning.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LearningCourseMapper {

    /** 获取每个学生的选课列表 */
    public Page<XcLearningCourse> findChooseCourseListPage (ChooseCourseResultList chooseCourseResultList);
}
