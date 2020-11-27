package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author: olw
 * @date: 2020/10/12 20:31
 * @description:  课程搜索服务
 */
@Api(value = "课程搜索")
public interface EsCourseControllerApi {
    @ApiOperation("课程搜索")
    public QueryResponseResult<CoursePub> list(int page,int size,
                                               CourseSearchParam courseSearchParam) throws IOException;
    @ApiOperation("根据课程id查询课程所有信息")
    public Map<String, CoursePub> getAll (String id);

    @ApiOperation("根据课程计划id获取关联的媒资管理信息")
    public TeachplanMediaPub getMedia (String teachplanId);

    @ApiOperation("根据课程计划ids获取关联的媒资管理信息")
    public QueryResponseResult getListMedia (String teachplanId);


}
