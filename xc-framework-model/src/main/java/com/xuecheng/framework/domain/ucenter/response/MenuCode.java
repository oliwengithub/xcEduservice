package com.xuecheng.framework.domain.ucenter.response;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;


/**
 * Created by admin on 2018/3/5.
 */
@ToString
public enum MenuCode implements ResultCode {
    MENU_CODE_EXIST(false,27000,"菜单code已经存在！"),
    MENU_CODE_NOTEXIST(false,23002,"菜单不存在！"),
    UCENTER_VERIFYCODE_NONE(false,23003,"请输入验证码！"),
    UCENTER_ACCOUNT_NOTEXISTS(false,23004,"账号不存在！"),
    UCENTER_CREDENTIAL_ERROR(false,23005,"账号或密码错误！"),
    UCENTER_LOGIN_ERROR(false,23006,"登陆过程出现异常请尝试重新操作！");

    //操作代码
    @ApiModelProperty(value = "操作是否成功", example = "true", required = true)
    boolean success;

    //操作代码
    @ApiModelProperty(value = "操作代码", example = "22001", required = true)
    int code;
    //提示信息
    @ApiModelProperty(value = "操作提示", example = "操作过于频繁！", required = true)
    String message;
    private MenuCode (boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, MenuCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, MenuCode> builder = ImmutableMap.builder();
        for (MenuCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
