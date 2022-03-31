package com.xuecheng.send;

import com.xuecheng.send.seivice.MailService;
import com.xuecheng.send.seivice.SendAccountService;
import com.xuecheng.send.seivice.VerificationCodeService;
import freemarker.template.TemplateException;
import freemarker.template.Version;
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
public class SendEmailTest {

    @Autowired @Qualifier("mailService")
    MailService mailService;

    @Autowired @Qualifier("verificationCodeService")
    VerificationCodeService verificationCodeService;

    @Autowired @Qualifier("sendAccountService")
    SendAccountService accountService;


    @Test
    public void sendEmail() {
        String s = RandomStringUtils.randomNumeric(15);
        mailService.sendSimpleMail("1768085488@qq.com", "测试", "1111");
    }

    @Test
    public void sendTemplate() throws TemplateException, IOException, MessagingException {
        String s = RandomStringUtils.randomNumeric(15);
        Map<String, Object> model = new HashMap<String, Object>(){{
            put("password", s);
            put("name", "oliwen");
            put("account", "oliwen");
        }};
        mailService.sendTemplateMail("1768085488@qq.com", "测试", "hello.html", model);
    }

    @Test
    public void sendEmailService() throws TemplateException, IOException, MessagingException {
        verificationCodeService.getVerificationCode("178263682@qq.com", "oliwen");
    }
    @Test
    public void sendAccountService() throws TemplateException, IOException, MessagingException {
        accountService.sendAccount("178263682@qq.com", "oliwen", "112323");
    }
}
