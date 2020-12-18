package com.xuecheng.learning.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.requset.CourseCommentResultList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseStateMapper {

    /**
     * 获取课程学习数量
     * @author: olw
     * @Date: 2020/12/18 12:27
     * @param courseId
     * @returns: int
    */
    public int getCourseUserNum (String courseId);

    /**
     * 获取课程收藏数量
     * @author: olw
     * @Date: 2020/12/18 12:30
     * @param courseId
     * @returns: int
    */
    public int getCourseFavoriteNum (String courseId);

}
