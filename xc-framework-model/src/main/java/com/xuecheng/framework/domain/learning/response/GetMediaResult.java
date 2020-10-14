package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class GetMediaResult extends ResponseResult {
    String mediaUrl;

    public GetMediaResult(ResultCode resultCode, String mediaUrl) {
        super(resultCode);
        this.mediaUrl = mediaUrl;
    }
}
