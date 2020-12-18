package com.xuecheng.learning.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.requset.CourseCommentResultList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseCommentMapper {

    Page<XcLearningCourseComment> getCourseCommentList(CourseCommentResultList courseCommentResultList);
}
