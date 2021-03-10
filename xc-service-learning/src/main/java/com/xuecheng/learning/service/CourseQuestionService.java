package com.xuecheng.learning.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import com.xuecheng.framework.domain.learning.ext.CourseQuestionExt;
import com.xuecheng.framework.domain.learning.ext.CourseQuestionGroupNode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.dao.CourseQuestionMapper;
import com.xuecheng.learning.dao.XcLearningCourseQuestionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author: olw
 * @date: 2020/12/13 15:58
 * @description:  课程问答业务
 */
@Service
public class CourseQuestionService {

    @Resource
    CourseQuestionMapper courseQuestionMapper;

    @Autowired
    XcLearningCourseQuestionRepository xcLearningCourseQuestionRepository;


    /**
     * 讨论分页接口
     * @author: olw
     * @Date: 2021/1/11 20:40
     * @param page
     * @param size
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult<com.xuecheng.framework.domain.learning.ext.CourseQuestionGroupNode>
    */
    public QueryResponseResult<CourseQuestionGroupNode> getCourseQuestionList (int page, int size) {
        page = page<=0 ? 1 : page;
        size = size<=0 ? 20 : size;
        PageHelper.startPage(page, size);
        Page<CourseQuestionGroupNode> courseQuestionList = courseQuestionMapper.getCourseQuestionList();
        QueryResult<CourseQuestionGroupNode> queryResult = new QueryResult<>();
        queryResult.setTotal(courseQuestionList.getTotal());
        List<CourseQuestionGroupNode> result = courseQuestionList.getResult();
        // 获取该问题所有相关的数据
        List<CourseQuestionGroupNode> collect = result.stream().peek(e -> e.setQuestionExts(getQuestionGroupList(e.getGroupId()))).collect(Collectors.toList());
        queryResult.setList(collect);
        return new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 获取分组所有数据
     * @author: olw
     * @Date: 2020/12/22 14:35
     * @param groupId
     * @returns: java.util.List<com.xuecheng.framework.domain.learning.ext.CourseQuestionExt>
    */
    public List<CourseQuestionExt> getQuestionGroupList (String groupId) {

        return courseQuestionMapper.getCourseQuestionGroup(groupId);
    }

    /**
     * 回复问题
     * @author: olw
     * @Date: 2020/12/22 14:47
     * @param xcLearningCourseQuestion
     * @returns: com.xuecheng.framework.model.response.ResponseResult
    */
    public ResponseResult add (XcLearningCourseQuestion xcLearningCourseQuestion) {
        if (xcLearningCourseQuestion == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String parentId = xcLearningCourseQuestion.getParentId();
        if (StringUtils.isEmpty(parentId)) {
            // 没有父级id设置为根节点
            xcLearningCourseQuestion.setParentId("0");
        }else {
            this.updateReplyNum(parentId);
        }
        xcLearningCourseQuestion.setCreateTime(new Date());
        xcLearningCourseQuestionRepository.save(xcLearningCourseQuestion);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 更新回复数量
     * @author: olw
     * @Date: 2021/1/11 20:48
     * @param id
     * @returns: void
    */
    private void updateReplyNum (String id){
        XcLearningCourseQuestion learningQuestion = this.getLearningQuestion(id);
        if (learningQuestion != null) {
            learningQuestion.setReplyCount(learningQuestion.getReplyCount() + 1);
            xcLearningCourseQuestionRepository.save(learningQuestion);
        }
    }

    private XcLearningCourseQuestion getLearningQuestion (String id) {
        return xcLearningCourseQuestionRepository.findById(id).orElse(null);
    }
}
