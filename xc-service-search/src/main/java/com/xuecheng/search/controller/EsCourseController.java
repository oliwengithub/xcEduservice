package com.xuecheng.search.controller;

import com.vividsolutions.jts.geomgraph.index.EdgeSetIntersector;
import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {
    @Autowired
    EsCourseService esCourseService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page,@PathVariable("size") int size, CourseSearchParam courseSearchParam) {
        return esCourseService.list(page,size,courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getAll (@PathVariable("id") String id) {
        // 将参数封装成数组参数方便接口参数拓展
        String[] ids = id.split(",");
        return esCourseService.getAll(ids);
    }

    @Override
    @GetMapping("/getmedia/{teachplanId}")
    public TeachplanMediaPub getMedia (@PathVariable("teachplanId") String teachplanId) {
        // 将参数封装成数组参数方便接口参数拓展
        String[] teachplanIds = new String[]{teachplanId};
        QueryResponseResult<TeachplanMediaPub> media = esCourseService.getMedia(teachplanIds);
        List<TeachplanMediaPub> teachplanMediaPubs = media.getQueryResult().getList();
        if (teachplanMediaPubs != null && teachplanMediaPubs.size() >  0 ){
            return teachplanMediaPubs.get(0);
        }
        return null;
    }

    @Override
    @GetMapping("/getlistmedia/{teachplanId}")
    public QueryResponseResult getListMedia (@PathVariable("teachplanId") String teachplanId) {
        // 将参数封装成数组参数方便接口参数拓展
        String[] teachplanIds = new String[]{teachplanId};
        return esCourseService.getMedia(teachplanIds);
    }
}
