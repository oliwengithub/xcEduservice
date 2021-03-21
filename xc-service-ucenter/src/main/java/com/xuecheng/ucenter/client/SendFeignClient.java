package com.xuecheng.ucenter.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author: olw
 * @date: 2021/3/11 16:58
 * @description:  消息发送客户端
 */
@FeignClient(XcServiceList.XC_SERVICE_SEND)
public interface SendFeignClient {

    @PostMapping("/send/code")
    public ResponseResult getCode (@RequestParam("account") String account, @RequestParam("username")String username);
}
