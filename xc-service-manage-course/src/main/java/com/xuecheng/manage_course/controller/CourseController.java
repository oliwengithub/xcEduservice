package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 课程管理模块
 * @author: olw
 * @date: 2019/8/24 9:55
 * @description:
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {

    @Autowired
    CourseService service;

    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachPlanNodeList (@PathVariable("courseId") String courseId) {
        return service.findTeacheplanList(courseId);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan (@RequestBody Teachplan teachplan) {
        return service.addTeachplan(teachplan);
    }
}
