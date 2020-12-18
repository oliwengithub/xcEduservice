package com.xuecheng.framework.domain.learning.requset;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 *
 * @author: olw
 * @date: 2020/10/30 21:14
 * @description:  查询用户选课列表参数
 */
@Data
@ToString
public class CourseCommentResultList extends RequestData {

    /**用户id*/
    private String userId;

    /**课程id*/
    private String courseId;

    /**评论状态*/
    private String status;

    /**起始分值*/
    private BigDecimal startScore;

    /**终止分值*/
    private BigDecimal endScore;



}
