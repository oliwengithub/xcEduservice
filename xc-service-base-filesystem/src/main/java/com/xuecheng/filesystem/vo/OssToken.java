package com.xuecheng.filesystem.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OssToken {
    private String region;
    private String accessKeyId;
    private String accessKeySecret;
    private String stsToken;
    private String bucket;

}
