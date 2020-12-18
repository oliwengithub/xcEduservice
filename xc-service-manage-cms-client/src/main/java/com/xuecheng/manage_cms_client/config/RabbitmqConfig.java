package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
public class RabbitmqConfig {

    /**
     * 交换机的名称
     */
    public static final String EX_ROUTING_CMS_POST_PAGE="ex_routing_cms_post_page";
    /**
     * 门户站点
     */
    public static final String QUEUE_CMS_POST_PAGE = "queue_cms_post_page";
    /**
    * 课程预览站点
    */
    public static final String QUEUE_CMS_PAGE_COURSE = "queue_cms_page_course";
    //routingKey 即站点Id
    @Value("${xuecheng.mq.routingKey01}")
    public  String routingKeyPage;
    @Value("${xuecheng.mq.routingKey02}")
    public  String routingKeyCourse;
    /**
     * 交换机配置使用direct类型
     * @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POST_PAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POST_PAGE).durable(true).build();
    }
    /**
     * 声明队列 页面发布
     * @author: olw
     * @Date: 2020/12/1 19:32
     * @returns: org.springframework.amqp.core.Queue
    */
    @Bean(QUEUE_CMS_POST_PAGE)
    public Queue QUEUE_CMS_POST_PAGE() {
        Queue queue = new Queue(QUEUE_CMS_POST_PAGE, true, false, false);
        return queue;
    }

    /**
     * 声明队列 课程发布
     * @author: olw
     * @Date: 2020/12/1 19:32
     * @returns: org.springframework.amqp.core.Queue
     */
    @Bean(QUEUE_CMS_PAGE_COURSE)
    public Queue QUEUE_CMS_PAGE_COURSE() {
        Queue queue = new Queue(QUEUE_CMS_PAGE_COURSE, true, false, false);
        return queue;
    }

    /**
     * 绑定队列到交换机
     *
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */

    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS01(@Qualifier(QUEUE_CMS_POST_PAGE) Queue queue, @Qualifier(EX_ROUTING_CMS_POST_PAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKeyPage).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS02(@Qualifier(QUEUE_CMS_PAGE_COURSE) Queue queue, @Qualifier(EX_ROUTING_CMS_POST_PAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKeyCourse).noargs();
    }
}
