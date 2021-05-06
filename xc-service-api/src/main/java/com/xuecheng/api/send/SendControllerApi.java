package com.xuecheng.api.send;

import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "消息发送接口")
public interface SendControllerApi {

    @ApiOperation("获取验证码")
    public ResponseResult getCode(String account, String username);

    @ApiOperation("发送账号相关信息")
    public ResponseResult sendAccount(String account, String name, String password);
}
