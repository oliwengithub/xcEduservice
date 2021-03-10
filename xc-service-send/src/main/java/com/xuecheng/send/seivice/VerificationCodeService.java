package com.xuecheng.send.seivice;

import com.xuecheng.framework.domain.Constants;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CheckUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author: olw
 * @date: 2021/3/10 15:51
 * @description:  验证码业务
 */
@Service
public class VerificationCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private SmsService smsService;

    /**
     * 获取邮箱或电话验证码
     * @author: olw
     * @Date: 2021/3/10 16:43
     * @param account
     * @returns: java.lang.String
    */
    public ResponseResult getVerificationCode(String account, String username) {
        if (StringUtils.isEmpty(account)) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String s = getCode(account);
        String code = StringUtils.isNotEmpty(s) ? s : RandomStringUtils.randomNumeric(6);
        //保存验证码
        boolean b = saveCode(account, code);
        if (b) {
            // 发送邮箱验证码
            if (CheckUtils.isEmail(account)) {
                Map<String, Object> model = new HashMap<>(3);
                model.put("username", username);
                model.put("code", code);
                try {
                    mailService.sendTemplateMail(account, "邮箱验证 | 在线教育网", "hello.html", model);
                }catch (Exception e){
                    return new ResponseResult(CommonCode.FAIL);
                }

            }else if (CheckUtils.isPhone(account)){
                boolean flag = smsService.sendSms(account, code);
                return flag ? new ResponseResult(CommonCode.SUCCESS) : new ResponseResult(CommonCode.FAIL);
            }
        }


        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 保存验证码
     * @author: olw
     * @Date: 2021/3/10 19:15
     * @param account
     * @param code
     * @returns: boolean
    */
    private boolean saveCode (String account, String code) {
        String key = "code_" + account;
        stringRedisTemplate.boundValueOps(key).set(code, 60, TimeUnit.SECONDS);

        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 获取验证码
     * @author: olw
     * @Date: 2021/3/10 19:15
     * @param account
     * @returns: boolean
     */
    private String getCode (String account) {
        String key = "code_" + account;
        String code = stringRedisTemplate.opsForValue().get(key);
        return code;
    }



}
