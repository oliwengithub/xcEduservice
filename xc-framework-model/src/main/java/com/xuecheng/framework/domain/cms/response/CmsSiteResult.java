package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;

import java.util.List;


@Data
public class CmsSiteResult extends ResponseResult {
    CmsSite cmsSites;
    public CmsSiteResult (ResultCode resultCode, CmsSite cmsSite) {
        super(resultCode);
        this.cmsSites = cmsSite;
    }
}
