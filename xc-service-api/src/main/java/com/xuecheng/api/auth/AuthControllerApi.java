package com.xuecheng.api.auth;


import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="权限认证接口",description = "权限认证接口")
public interface AuthControllerApi {

    @ApiOperation("登录认证")
    public LoginResult login(LoginRequest loginRequest);

    @ApiOperation("退出登录")
    public ResponseResult logout();

    @ApiOperation("查询jwt令牌")
    public JwtResult userJwt();
}
