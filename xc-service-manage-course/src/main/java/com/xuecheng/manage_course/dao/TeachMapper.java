package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator.
 */
@Mapper
public interface TeachMapper {
   /**
    * 查询课程计划菜单列表
    * @author: olw
    * @Date: 2020/12/18 13:04
    * @param courseId
    * @returns: com.xuecheng.framework.domain.course.ext.TeachplanNode
   */
   public TeachplanNode selectList (String courseId);
}
