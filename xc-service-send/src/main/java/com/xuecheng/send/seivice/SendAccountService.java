package com.xuecheng.send.seivice;

import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author: olw
 * @date: 2021/4/9 16:25
 * @description:  发送账号
 */
@Service
public class SendAccountService {

    @Autowired
    MailService mailService;

    public ResponseResult sendAccount (String account, String name, String password) {
        Map<String, Object> map = new HashMap<String, Object>(3){{
            put("account",account);
            put("name",name);
            put("password",password);
        }};
        try {
            mailService.sendTemplateMail(account, "IT职业教育网", "hello.html", map);

        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
