package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.request.RequestCompanyList;
import com.xuecheng.framework.domain.ucenter.response.CompanyResult;
import com.xuecheng.framework.domain.ucenter.response.MenuResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value = "机构管理")
public interface CompanyControllerApi {

    @ApiOperation("获取所有机构列表")
    public QueryResponseResult findAllCompany (int page, int size, RequestCompanyList requestCompanyList);

    @ApiOperation("添加机构")
    public ResponseResult add (XcCompany xcCompany);

    @ApiOperation("获取机构")
    public CompanyResult getCompanyInfo (String companyId);

    @ApiOperation("更新机构")
    public ResponseResult  edit (XcCompany xcCompany);

    @ApiOperation("获取所有机构")
    public List<XcCompany> findAll ();

}
