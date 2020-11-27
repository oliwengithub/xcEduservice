package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.UserInfo;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.domain.ucenter.request.UserListRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "用户中心")
public interface UcenterControllerApi {

    @ApiOperation("获取用户信息")
    public XcUserExt getUserExt (String username);

    @ApiOperation("获取个人信息")
    public UserInfo getUserInfo (String userId);

    @ApiOperation("个人信息修改")
    public ResponseResult edit (XcUser xcUser);

    @ApiOperation("获取所有用户列表")
    public QueryResponseResult findUserList (int page, int size, UserListRequest userListRequest);

    @ApiOperation("修改用户信息")
    public ResponseResult editInfo (UserInfo userInfo);

    @ApiOperation("添加用户")
    public ResponseResult add (UserInfo userInfo);

    @ApiOperation("更新用户状态")
    public ResponseResult updateStatus (XcUser xcUser);

    @ApiOperation("重置用户密码")
    public ResponseResult resetPassword ( XcUser xcUser);

}
