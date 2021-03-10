package com.xuecheng.learning.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import com.xuecheng.framework.domain.learning.ext.CourseQuestionExt;
import com.xuecheng.framework.domain.learning.ext.CourseQuestionGroupNode;
import com.xuecheng.framework.domain.learning.requset.CourseCommentResultList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseQuestionMapper {

    /**
     * 获取根节点的parentId = 0 的所有提问
     * @author: olw
     * @Date: 2020/12/20 20:20
     * @param
     * @returns: com.github.pagehelper.Page<com.xuecheng.framework.domain.learning.ext.CourseQuestionGroupNode>
    */
    Page<CourseQuestionGroupNode> getCourseQuestionList ();

    /**
     * 根据groupId获取当前问题分组所有的回复
     * @author: olw
     * @Date: 2020/12/20 20:21
     * @param   groupId
     * @returns: java.util.List<com.xuecheng.framework.domain.learning.ext.CourseQuestionExt>
    */
    List<CourseQuestionExt> getCourseQuestionGroup (String groupId);


}
