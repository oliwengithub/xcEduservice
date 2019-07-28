package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value="cms站点管理接口",description = "cms站点管理接口，提供站点的增、删、改、查")
public interface CmsSiteControllerApi {

    @ApiOperation("分页查询站点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })

    //站点查询
    public QueryResponseResult findList (int page, int size , QueryPageRequest queryPageRequest);

    @ApiOperation("新增站点")
    public CmsSiteResult add(CmsSite cmsSite);

    //根据页面id查询页面信息
    @ApiOperation("根据站点id查站点信息")
    public CmsSite findById (String id);
    //修改页面
    @ApiOperation("修改站点")
    public CmsSiteResult edit(String id, CmsSite cmsSite);

    //删除页面
    @ApiOperation("删除站点")
    public ResponseResult delete (String id);

    //查询所有站点信息
    @ApiOperation("查询所有站点一级菜单")
    public List<CmsSite> findAll();
}
