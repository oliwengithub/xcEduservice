package com.xuecheng.send.controller;

import com.xuecheng.api.send.SendControllerApi;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.send.seivice.SendAccountService;
import com.xuecheng.send.seivice.VerificationCodeService;
import org.apache.tomcat.util.net.SendfileState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    SendAccountService sendAccountService;

    @PostMapping("/code")
    @Override
    public ResponseResult getCode (@RequestParam("account")String account, @RequestParam("username")String username) {
        return verificationCodeService.getVerificationCode(account, username);
    }
    @PostMapping("/account")
    @Override
    public ResponseResult sendAccount (@RequestParam("account")String account, @RequestParam("name")String name, @RequestParam("password")String password) {
        return sendAccountService.sendAccount(account, name, password);
    }
}
