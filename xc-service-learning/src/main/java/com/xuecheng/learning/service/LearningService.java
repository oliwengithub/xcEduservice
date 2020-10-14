package com.xuecheng.learning.service;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSearchClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author: olw
 * @date: 2020/10/13 16:33
 * @description:  学习中心
 */
@Service
public class LearningService {

    @Autowired
    private CourseSearchClient courseSearchClient;


    public GetMediaResult getMedia (String courseId, String teachplanId) {

        // 校验学生权限 ....

        // 远程调用获取课程计划对应的媒资地址
        TeachplanMediaPub media = courseSearchClient.getMedia(teachplanId);
        if (media == null || StringUtils.isEmpty(media.getMediaUrl())) {
            // 获取视频播放地址出错
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }

        return new GetMediaResult(CommonCode.SUCCESS, media.getMediaUrl());
    }
}
