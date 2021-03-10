package com.xuecheng.send;

import com.xuecheng.api.send.SendControllerApi;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息发送服务
 * @author: olw
 * @date: 2021/3/10 20:17
 * @description:
 */

@RestController
@RequestMapping("/send")
public class SendController implements SendControllerApi {

    @PostMapping("/code")
    @Override
    public ResponseResult getCode (String account, String username) {
        return null;
    }
}
