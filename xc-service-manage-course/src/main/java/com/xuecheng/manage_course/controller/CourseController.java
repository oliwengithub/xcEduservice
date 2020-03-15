package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.cms.response.CoursePreviewResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理模块
 *
 * @author: olw
 * @date: 2019/8/24 9:55
 * @description:
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanNodeList (@PathVariable("courseId") String courseId) {
        return courseService.findTeacheplanList(courseId);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan (@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById ( @PathVariable("courseId") String courseId) throws RuntimeException {
        return courseService.getCoursebaseById(courseId);
    }

    @Override
    @PutMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase (@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return courseService.updateCoursebase(id, courseBase);
    }

    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById (@PathVariable("courseId") String courseId) {
        return courseService.getCourseMarketById(courseId);
    }

    @Override
    @PostMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarket (@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        CourseMarket market = courseService.updateCourseMarket(id, courseMarket);
        System.out.println(market.toString());
        if (market != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        }else {

            return new ResponseResult(CommonCode.FAIL);
        }
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList (@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return courseService.findCourseList(page, size, courseListRequest);
    }

    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        //保存课程图片
        return courseService.saveCoursePic(courseId,pic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePicByCourseId(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseView (@PathVariable("id") String id) {
        return courseService.getCourseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePreviewResult preview (@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish (@PathVariable("id") String id) {
        return courseService.publish(id);
    }
}
