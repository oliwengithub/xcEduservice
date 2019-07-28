package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsSiteResult;
import com.xuecheng.framework.domain.cms.response.CmsTemplateResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value="cms模板管理接口",description = "cms模板管理接口，提供模板的增、删、改、查")
public interface CmsTemplateControllerApi {

    @ApiOperation("分页查询模板列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
            @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
    })

    //站点查询
    public QueryResponseResult findList (int page, int size , QueryPageRequest queryPageRequest);

    @ApiOperation("新增模板")
    public CmsTemplateResult add(CmsTemplate cmsTemplate);

    //根据模板id查询模板
    @ApiOperation("根据模板id查模板信息")
    public CmsTemplate findById (String id);
    //修改模板
    @ApiOperation("修改模板")
    public CmsTemplateResult edit(String id, CmsTemplate cmsTemplate);

    //删除模板
    @ApiOperation("删除模板")
    public ResponseResult delete (String id);

    //查询所有模板信息
    @ApiOperation("查询所有模板一级菜单")
    public List<CmsTemplate> findAll();
}
