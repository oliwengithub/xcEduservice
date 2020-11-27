package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.XcRole;
import com.xuecheng.framework.domain.ucenter.ext.RoleExt;
import com.xuecheng.framework.domain.ucenter.response.RoleResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = "角色管理",description = "角色管理")
public interface RoleControllerApi {

    @ApiOperation("获取所有角色列表")
    public QueryResponseResult  findAllRole ();


    @ApiOperation("添加角色")
    public ResponseResult add (RoleExt roleExt);

    @ApiOperation("获取角色")
    public RoleResult getRoleInfo (String roleId);

    @ApiOperation("更新角色")
    public ResponseResult  edit (RoleExt roleExt);

    @ApiOperation("删除角色")
    public ResponseResult updateStatus (XcRole xcRole);


}
