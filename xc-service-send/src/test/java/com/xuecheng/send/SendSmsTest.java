package com.xuecheng.send;

import com.xuecheng.send.seivice.MailService;
import com.xuecheng.send.seivice.SmsService;
import com.xuecheng.send.seivice.VerificationCodeService;
import freemarker.template.TemplateException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SendSmsTest {


    @Autowired @Qualifier("verificationCodeService")
    VerificationCodeService verificationCodeService;


    @Test
    public void sendEmail() {
        verificationCodeService.getVerificationCode("178263682@qq.com", "oliwen");
        verificationCodeService.getVerificationCode("18897956300", "oliwen");
    }

}
