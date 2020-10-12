package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator.
 */
public interface TeachplanMediaRepository extends JpaRepository<TeachplanMedia,String> {

    /**
     *  根据课程id获取所有的课程媒资关联信息
     * @author: olw
     * @Date: 2020/10/11 17:21
     * @param courseId
     * @returns: java.util.List<com.xuecheng.framework.domain.course.TeachplanMedia>
    */
    public List<TeachplanMedia> findByCourseId(String courseId);
 }
