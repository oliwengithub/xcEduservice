package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author: olw
 * @date: 2020/11/29 20:23
 * @description:
 */
@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_COURSE)
public interface CourseClient {

    /**
     * 获取课程营销信息
     * @param courseId
     */
    @GetMapping("/course/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById (@PathVariable("courseId") String courseId);

    /**
     * 获取单个课程计划
     * @author: olw
     * @Date: 2020/11/30 20:45
     * @param teachplanId
     * @returns: com.xuecheng.framework.domain.course.Teachplan
    */
    @GetMapping("/course/getteachplan/{teachplanId}")
    public Teachplan getCourseTeachplan (@PathVariable("teachplanId") String teachplanId);

    /**
     * 获取课程基本信息
     * @author: olw
     * @Date: 2020/12/13 17:23
     * @param courseId
     * @returns: com.xuecheng.framework.domain.course.CourseBase
    */
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById (@PathVariable("courseId") String courseId);
}
