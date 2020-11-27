package com.xuecheng.framework.domain.ucenter.response;

import com.xuecheng.framework.domain.ucenter.XcCompany;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CompanyResult extends ResponseResult {
    XcCompany xcCompany;

    public CompanyResult (ResultCode resultCode, XcCompany xcCompany) {
        super(resultCode);
        this.xcCompany = xcCompany;
    }
}
