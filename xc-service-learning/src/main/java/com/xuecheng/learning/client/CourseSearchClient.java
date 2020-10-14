package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author: olw
 * @date: 2020/10/13 16:22
 * @description: 搜索服务客户端接口
 */
@FeignClient(value = XcServiceList.XC_SERVICE_SEARCH)
public interface CourseSearchClient {

    @GetMapping(value="/search/course/getmedia/{teachplanId}")
    public TeachplanMediaPub getMedia(@PathVariable("teachplanId") String teachplanId);
}
