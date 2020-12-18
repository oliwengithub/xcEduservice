package com.xuecheng.learning.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.learning.XcLearningCourseComment;
import com.xuecheng.framework.domain.learning.XcLearningCourseState;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.CourseStateMapper;
import com.xuecheng.learning.dao.XcLearningCourseCommentRepository;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcLearningCourseStateRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author: olw
 * @date: 2020/12/16 17:18
 * @description:  课程统计业务
 */
@Service
public class CourseStateService {

    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;

    @Resource
    CourseStateMapper courseStateMapper;

    @Autowired
    CourseSearchClient courseSearchClient;

    @Autowired
    XcLearningCourseCommentRepository xcLearningCourseCommentRepository;

    @Autowired
    XcLearningCourseStateRepository xcLearningCourseStateRepository;

    @Autowired
    public void updateCourseState() {
        int page = 1;
        int size = 100;
        // 获取需要更新的课程
        while (true){
            QueryResult<CoursePub> queryResult = getAllCoursePub(page, size);
            List<CoursePub> list = queryResult.getList();
            List<XcLearningCourseState> courseStateList = new ArrayList<>(list.size());
            // 这个是页码
            long total = queryResult.getTotal();
            if (total - page > -1) {
                page = page +1;
                // 开始获取课程统计相关数据
                list.forEach(e->{
                    // 创建课程统计对象
                    String courseId = e.getId();
                    XcLearningCourseState courseState = createCourseState(courseId);
                    double courseDuration = getCourseDuration(e.getTeachplan());
                    courseState.setCourseDuration(courseDuration);
                    courseStateList.add(courseState);

                });

                // 批量更新课程统计数据
                batchSaveCourseState(courseStateList);

            }else {
                break;
            }
        }


    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSaveCourseState (List<XcLearningCourseState> courseStateList) {
        xcLearningCourseStateRepository.saveAll(courseStateList);
    }

    /**
     * 创建课程统计对象
     * @author: olw
     * @Date: 2020/12/18 17:01
     * @param courseId
     * @returns: com.xuecheng.framework.domain.learning.XcLearningCourseState
    */
    private XcLearningCourseState createCourseState(String courseId) {

        XcLearningCourseState courseState = new XcLearningCourseState();
        // 获取课程学习人数
        int chooseNum = this.getChooseCourseUserNum(courseId);
        // 获取收藏数量
        int favoriteNum = this.getCourseFavoriteNum(courseId);
        // 获取课程评分
        Map<String, BigDecimal> courseCommentScore = getCourseCommentScore(courseId);
        // 平均评分
        BigDecimal score = courseCommentScore.get("score");
        courseState.setCourseId(courseId);
        courseState.setScore(score);
        courseState.setFavoriteNum(favoriteNum);
        courseState.setChooseNum(chooseNum);
        return courseState;
    }

    /**
     * 获取课程时长
     * @author: olw
     * @Date: 2020/12/18 14:46
     * @param teachplan
     * @returns: long
    */
    private double getCourseDuration(String teachplan) {
        if (StringUtils.isEmpty(teachplan)) {
            return 0d;
        }
        TeachplanNode teachplanNode = JSON.parseObject(teachplan, TeachplanNode.class);
        List<TeachplanNode> children = teachplanNode.getChildren();
        return children.stream().map(c ->
                c.getChildren().stream().filter(t -> t.getTimelength() != null).mapToDouble(TeachplanNode::getTimelength).summaryStatistics()
        ).mapToDouble(DoubleSummaryStatistics::getSum).sum();
    }

    /**
     * 获取课程评分相关数据
     * @author: olw
     * @Date: 2020/12/18 13:41
     * @param courseId
     * @returns: java.math.BigDecimal
    */
    public Map<String, BigDecimal> getCourseCommentScore(String courseId) {
        Map<String, BigDecimal> decimalMap = new HashMap<>();
        String status = "205001";
        // 获取课程所有有效评论
        List<XcLearningCourseComment> courseComments = xcLearningCourseCommentRepository.findByCourseIdAndStatus(courseId, status);
        if (!(courseComments != null && courseComments.size() > 0)) {
            decimalMap.put("score", new BigDecimal(0));
            decimalMap.put("total", new BigDecimal(0));
            return decimalMap;
        }
        // 获取课程的平均评分
        BigDecimal score = courseComments.stream().map(XcLearningCourseComment::getScore).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(courseComments.size()), 1, BigDecimal.ROUND_HALF_UP);
        // 获取总分
        BigDecimal total = score.multiply(BigDecimal.valueOf(courseComments.size()));
        decimalMap.put("score", score);
        decimalMap.put("total", total);
        // 统计每个评分段的数量 0-1 1-2 2-3 3-4 4-5
        decimalMap.put("oneStart", new BigDecimal(1));
        decimalMap.put("twoStart", new BigDecimal(1));
        decimalMap.put("threeStart", new BigDecimal(1));
        decimalMap.put("fourStart", new BigDecimal(1));
        decimalMap.put("fiveStart", new BigDecimal(1));
        return decimalMap;
    }

    /**
     * 获取选课学习人数
     * @author: olw
     * @Date: 2020/12/18 13:33
     * @param courseId
     * @returns: int
    */
    private int getChooseCourseUserNum (String courseId) {
        return courseStateMapper.getCourseUserNum(courseId);
    }

    /**
     * 获取收藏人数
     * @author: olw
     * @Date: 2020/12/18 13:36
     * @param courseId
     * @returns: int
    */
    private int getCourseFavoriteNum (String courseId) {
        return courseStateMapper.getCourseFavoriteNum(courseId);
    }

    /**
     * 获取所有发布的课程
     * @author: olw
     * @Date: 2020/12/16 17:47
     * @param page
     * @param size
     * @returns: com.xuecheng.framework.model.response.QueryResult<com.xuecheng.framework.domain.course.CoursePub>
    */
    private QueryResult<CoursePub> getAllCoursePub (int page, int size) {
        QueryResponseResult<CoursePub> list = courseSearchClient.list(page, size);
        QueryResult<CoursePub> queryResult = list.getQueryResult();
        // 获取总条数
        long total = queryResult.getTotal();
        // 计算总页数
        long pageNum = 0;
        if (total%size == 0) {
            pageNum =  total/size;
        }else {
            pageNum = total/size +1;
        }
        queryResult.setTotal(pageNum);
        return queryResult;
    }

}
