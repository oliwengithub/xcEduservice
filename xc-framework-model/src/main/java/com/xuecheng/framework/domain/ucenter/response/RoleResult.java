package com.xuecheng.framework.domain.ucenter.response;

import com.xuecheng.framework.domain.ucenter.ext.RoleExt;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
public class RoleResult  extends ResponseResult {
    private RoleExt roleExt;

    public RoleResult (ResultCode resultCode, RoleExt roleExt) {
        super(resultCode);
        this.roleExt = roleExt;

    }
}
