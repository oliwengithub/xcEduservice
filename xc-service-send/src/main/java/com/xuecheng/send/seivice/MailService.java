package com.xuecheng.send.seivice;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author: olw
 * @date: 2021/3/9 20:38
 * @description:  邮件发送服务
 */
@Service
public class MailService {

    /**
     * Spring官方提供的集成邮件服务的实现类，目前是Java后端发送邮件和集成邮件服务的主流工具。
     */
    @Resource
    private JavaMailSender mailSender;

    /**
     * 从配置文件中注入发件人的姓名
     */
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 发送文本邮件
     * @author: olw
     * @Date: 2021/3/9 21:13
     * @param to
     * @param subject
     * @param content
    */
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        //发件人
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    /**
     * 发送html邮件
     * @author: olw
     * @Date: 2021/3/9 21:14
     * @param to
     * @param subject
     * @param content
    */
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        //注意这里使用的是MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        //第二个参数：格式是否为html
        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * 发送模板邮件
     * @author: olw
     * @Date: 2021/3/9 21:15
     * @param to 收件人
     * @param subject 标题
     * @param template  模板
     * @param model 数据模型
    */
    public void sendTemplateMail(String to, String subject, String template, Map<String, Object> model) throws IOException, TemplateException, MessagingException {
        // 获得模板
        Template template1 = freeMarkerConfigurer.getConfiguration().getTemplate(template);
        // 使用Map作为数据模型，定义属性和值
        model.put("myname","Ray。");
        // 传入数据模型到模板，替代模板中的占位符，并将模板转化为html字符串
        String templateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template1,model);
        // 该方法本质上还是发送html邮件，调用之前发送html邮件的方法
        this.sendHtmlMail(to, subject, templateHtml);
    }

    /**
     * 发送带附件的邮件
     * @author: olw
     * @Date: 2021/3/9 21:16
     * @param to
     * @param subject
     * @param content
     * @param filePath
    */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        //要带附件第二个参数设为true
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        FileSystemResource file = new FileSystemResource(new File(filePath));
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
        helper.addAttachment(fileName, file);
        mailSender.send(message);
    }
}
