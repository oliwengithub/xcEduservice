package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.cms.response.CoursePreviewResult;
import com.xuecheng.framework.domain.cms.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class CourseController extends BaseController implements CourseControllerApi   {

    @Autowired
    CourseService courseService;

    @PreAuthorize("hasAuthority('xc_teachmanager_course_teachplan_list')")
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanNodeList (@PathVariable("courseId") String courseId) {
        return courseService.findTeacheplanList(courseId);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_teachplan_add')")
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan (@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById ( @PathVariable("courseId") String courseId)  {
        return courseService.getCoursebaseById(courseId);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_base')")
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

    @PreAuthorize("hasAuthority('xc_teachmanager_course_market')")
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


    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList (@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        XcOauth2Util.UserJwt jwt = XcOauth2Util.getUserJwtFromHeader(request);
        // 对应机构教师查询课程列表
        if (jwt != null) {
            courseListRequest.setCompanyId(jwt.getCompanyId());
            courseListRequest.setUserId(jwt.getId());
        }
        return courseService.findCourseList(page, size, courseListRequest);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_add')")
    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_pic')")
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic") String pic) {
        //保存课程图片
        return courseService.saveCoursePic(courseId,pic);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_pic')")
    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePicByCourseId(courseId);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_pic')")
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

    @PreAuthorize("hasAuthority('xc_teachmanager_course_preview')")
    @Override
    @PostMapping("/preview/{id}")
    public CoursePreviewResult preview (@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_publish')")
    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish (@PathVariable("id") String id) {
        return courseService.publish(id);
    }

    @PreAuthorize("hasAuthority('xc_teachmanager_course_media')")
    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia (@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }

    @Override
    @GetMapping("/getteachplan/{teachplanId}")
    public Teachplan getCourseTeachplan (@PathVariable("teachplanId") String teachplanId) {
        return courseService.getCourseTeachplan(teachplanId);
    }
}
