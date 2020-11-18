package com.xuecheng.framework.domain.ucenter.response;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class MenuResult extends ResponseResult {
    XcMenu xcMenu;

    public MenuResult (ResultCode resultCode,  XcMenu xcMenu) {
        super(resultCode);
        this.xcMenu = xcMenu;
    }
}
