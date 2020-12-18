package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.config.RabbitmqConfig;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听MQ，接收页面发布消息 消费方
 * @author Administrator
 * @version 1.0
 **/
@Component
public class ConsumerPostPage {

    @Autowired
    PageService pageService;

    /**
     * 页面发布
     * @author: olw
     * @Date: 2020/12/9 15:41
     * @param msg
     * @returns: void
    */
    @RabbitListener(queues = {RabbitmqConfig.QUEUE_CMS_POST_PAGE, RabbitmqConfig.QUEUE_CMS_PAGE_COURSE})
    public void postPage(String msg){
        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        //得到消息中的页面id
        String pageId = (String) map.get("pageId");
        //调用service方法将页面从GridFs中下载到服务器
        pageService.savePageToServerPath(pageId);

    }

}
