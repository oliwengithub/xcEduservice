package com.xuecheng.learning.controller;

import com.xuecheng.api.learnning.ChooseCourseControllerApi;
import com.xuecheng.framework.domain.learning.requset.ChooseCourseResultList;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.learning.response.LearningCourseResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.learning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author: olw
 * @date: 2020/10/30 21:17
 * @description:  用户选课
 */
@RestController
@RequestMapping("/learning")
public class ChooseCourseController extends BaseController implements ChooseCourseControllerApi {


    @Autowired
    LearningService learningService;

    @Override
    @GetMapping("/choosecourse/list/{page}/{size}")
    public QueryResponseResult getChooseCourseList (@PathVariable("page") int page, @PathVariable("size") int size, ChooseCourseResultList chooseCourseResultList) {

        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt != null) {
            chooseCourseResultList.setUserId(userJwt.getId());
        }
        return learningService.getChooseCourseList(page, size, chooseCourseResultList);
    }

    @Override
    @PostMapping("/choosecourse/addopencourse/{courseId}")
    public ResponseResult addOpenCourse (@PathVariable("courseId") String courseId) {
        XcOauth2Util.UserJwt userJwt = XcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt == null) {
           return new ResponseResult(LearningCode.CHOOSECOURSE_USERISNULL);
        }
        return learningService.addOpenCourse(courseId, userJwt.getId());
    }

    @Override
    @GetMapping("/choosecourse/learnstatus/{courseId}")
    public LearningCourseResult queryLearnStatus (@PathVariable("courseId") String courseId) {
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt == null) {
            return new LearningCourseResult(LearningCode.CHOOSECOURSE_USERISNULL, null);
        }
        return learningService.queryLearnStatus(courseId, userJwt.getId());
    }
}
