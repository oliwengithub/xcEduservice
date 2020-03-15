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

    //队列bean的名称
    //门户站点
    public static final String QUEUE_CMS_POSTPAGE01 = "queue_cms_postpage01";
    //课程预览站点
    public static final String QUEUE_CMS_POSTPAGE02 = "queue_cms_postpage02";
    //交换机的名称
    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";
    //队列的名称
    @Value("${xuecheng.mq.queue01}")
    public  String queue_cms_postpage_name01;
    @Value("${xuecheng.mq.queue02}")
    public  String queue_cms_postpage_name02;
    //routingKey 即站点Id
    @Value("${xuecheng.mq.routingKey01}")
    public  String routingKey01;
    @Value("${xuecheng.mq.routingKey02}")
    public  String routingKey02;
    /**
     * 交换机配置使用direct类型
     * @return the exchange
     */
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }
    //声明队列
    @Bean(QUEUE_CMS_POSTPAGE01)
    public Queue QUEUE_CMS_POSTPAGE01() {
        Queue queue = new Queue(queue_cms_postpage_name01);
        return queue;
    }

    //声明队列
    @Bean(QUEUE_CMS_POSTPAGE02)
    public Queue QUEUE_CMS_POSTPAGE02() {
        Queue queue = new Queue(queue_cms_postpage_name02);
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
    public Binding BINDING_QUEUE_INFORM_SMS01(@Qualifier(QUEUE_CMS_POSTPAGE01) Queue queue, @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey01).noargs();
    }

    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS02(@Qualifier(QUEUE_CMS_POSTPAGE02) Queue queue, @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey02).noargs();
    }
}
