package com.xuecheng.learning.controller;

import com.xuecheng.api.learnning.CourseLearningControllerApi;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.learning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author: olw
 * @date: 2020/10/13 16:55
 * @description:  学习服务
 */
@RestController
@RequestMapping("/learning/course")
public class CourseLearningController extends BaseController implements CourseLearningControllerApi {

    @Autowired
    private LearningService learningService;

    @Override
    @GetMapping("/getmedia/{courseId}/{teachplanId}")
    public GetMediaResult getMedia (@PathVariable("courseId") String courseId, @PathVariable("teachplanId")String teachplanId) {
        XcOauth2Util xcOauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt userJwt = xcOauth2Util.getUserJwtFromHeader(request);
        String userId = "";
        if (userJwt != null) {
            userId = userJwt.getId();
        }
        return learningService.getMedia(userId, courseId, teachplanId);
    }
}
