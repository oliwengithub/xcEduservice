package com.xuecheng.send.seivice;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信验证码
 * @author: olw
 * @date: 2021/3/10 21:03
 * @description:
 */
@Service
public class SmsService {

    @Value("${aliyun.accessKeyID}")
    private String accessKeyID;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    public boolean sendSms(String phoneNum, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen", accessKeyID, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-shenzhen");
        request.putQueryParameter("SignName", "乐购");
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("TemplateCode", "SMS_205405521");

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);

        request.putQueryParameter("TemplateParam", JSON.toJSONString(params));

        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getHttpResponse().isSuccess();

        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
