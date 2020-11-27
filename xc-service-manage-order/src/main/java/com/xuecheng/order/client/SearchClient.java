package com.xuecheng.order.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.CoursePub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 *
 * @author: olw
 * @date: 2020/11/4 16:44
 * @description:  搜索服务客户端
 */
@FeignClient(XcServiceList.XC_SERVICE_SEARCH)
public interface SearchClient {

    @GetMapping("/search/course/getall/{id}")
    public Map<String, CoursePub> getAll (@PathVariable("id") String id);
}
