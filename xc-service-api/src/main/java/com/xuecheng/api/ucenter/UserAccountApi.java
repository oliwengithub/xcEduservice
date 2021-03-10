package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("用户账户安全")
public interface UserAccountApi {

    @ApiOperation("绑定手机")
    public ResponseResult bindPhone (String phone, String code);

    @ApiOperation("解除绑定")
    public ResponseResult removeBind (String account);

    @ApiOperation("绑定邮箱")
    public ResponseResult bindEmail (String Email, String code);

    @ApiOperation("修改密码")
    public ResponseResult editPass (String newPass, String oldPass);

    @ApiOperation("获取验证码")
    public ResponseResult getCode (String account);




}
