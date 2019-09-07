package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="课程管理接口",description = "课程管理接口，提供课程的增、删、查、改接口")
public interface CourseControllerApi {

    //查询三级菜单
    @ApiOperation("查询课程计划三级菜单")
    public TeachplanNode findTeachPlanNodeList(String courseId);

    //添加课程计划
    public ResponseResult addTeachplan(Teachplan teachplan);
}
