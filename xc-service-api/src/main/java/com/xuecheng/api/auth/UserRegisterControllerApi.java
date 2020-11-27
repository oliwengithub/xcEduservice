package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "用户注册接口")
public interface UserRegisterControllerApi {

    @ApiOperation("用户注册")
    public ResponseResult register (XcUser xcUser);

    @ApiOperation("检验用户名是否存在")
    public ResponseResult checkUsername (String username);

}
