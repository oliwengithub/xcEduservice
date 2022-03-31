package com.xuecheng.filesystem.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.xuecheng.filesystem.config.AliOssConfig;
import com.xuecheng.filesystem.vo.OssToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OssService {

    @Autowired
    AliOssConfig aliOssConfig;
    public OssToken getToken () throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(aliOssConfig.getRegion(),
                aliOssConfig.getAccessKeyId(),
                aliOssConfig.getAccessKeySecret());
        DefaultAcsClient client = new DefaultAcsClient(profile);
        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleSessionName("oliwen");
        request.setRoleArn(aliOssConfig.getRoleArn());
        request.setDurationSeconds(1000L);
        try {
            AssumeRoleResponse acsResponse = client.getAcsResponse(request);
            //拿到前端需要的数据了
            AssumeRoleResponse.Credentials credentials = acsResponse.getCredentials();
            String accessKeyId = credentials.getAccessKeyId();
            String accessKeySecret = credentials.getAccessKeySecret();
            String securityToken = credentials.getSecurityToken();
            //构建前端需要的vo
            return OssToken.builder()
                    .accessKeyId(accessKeyId)
                    .accessKeySecret(accessKeySecret).stsToken(securityToken)
                    .region("oss-" + aliOssConfig.getRegion()).bucket(aliOssConfig.getBucket())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
