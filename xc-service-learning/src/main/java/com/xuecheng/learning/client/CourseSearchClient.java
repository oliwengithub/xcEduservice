package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

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

    @GetMapping("/search/course/getall/{id}")
    public Map<String, CoursePub> getAll (@PathVariable("id") String id);

    /**
     * 分页搜索课程成
     * @author: ol w
     * @Date: 2020/12/18 12:03
     * @param page
     * @param size
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult<com.xuecheng.framework.domain.course.CoursePub>
    */
    @GetMapping("/search/course/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page, @PathVariable("size") int size);
}
