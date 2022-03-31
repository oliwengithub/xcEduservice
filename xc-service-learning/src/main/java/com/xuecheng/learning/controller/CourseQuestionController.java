package com.xuecheng.learning.controller;

import com.xuecheng.api.learnning.LearningCourseQuestionControllerApi;
import com.xuecheng.framework.domain.learning.XcLearningCourseQuestion;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.Oauth2Util;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.learning.service.CourseQuestionService;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author: olw
 * @date: 2020/12/22 14:08
 * @description:  课程问答
 */
@RestController
@RequestMapping("/learning/question")
public class CourseQuestionController extends BaseController implements LearningCourseQuestionControllerApi {

    @Autowired
    CourseQuestionService courseQuestionService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult getCourseQuestionList (@PathVariable("page") int page,@PathVariable("size") int size) {
        return courseQuestionService.getCourseQuestionList(page, size);
    }

    @Override
    @PostMapping("/apply")
    public ResponseResult applyQuestion (@RequestBody XcLearningCourseQuestion xcLearningCourseQuestion) {
        XcOauth2Util.UserJwt userJwt = XcOauth2Util.getUserJwtFromHeader(request);
        if (userJwt == null) {
            ExceptionCast.cast(CommonCode.UNAUTHENTICATED);
        }
        xcLearningCourseQuestion.setUserId(userJwt.getId());
        return courseQuestionService.add(xcLearningCourseQuestion);
    }
}
