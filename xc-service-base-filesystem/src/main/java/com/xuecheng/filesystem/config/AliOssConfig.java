package com.xuecheng.filesystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author: olw
 * @date: 2021/5/9 13:34
 * @description:  阿里云配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "alioss")
public class AliOssConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String roleArn;
    private String region;
    private String bucket;

}
