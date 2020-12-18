package com.xuecheng.learning.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author: olw
 * @date: 2020/11/30 16:57
 * @description:  mq配置
 */
@Configuration
public class RabbitMQConfig {


    /**
     * 更新课程学习进度交换机
     */
    public static final String EX_LEARNING_SCHEDULE = "ex_learning_schedule";

    /**
     * 更新课程学习进度队列
     */
    public static final String XC_LEARNING_SCHEDULE = "xc_learning_schedule";

    /**
     * 更新课程学习进度路由key
     */
    public static final String XC_LEARNING_SCHEDULE_KEY = "schedule_key";

    /**
     * 添加选课任务交换机
    */
    public static final String EX_LEARNING_ADDCHOOSECOURSE = "ex_learning_addchoosecourse";

    /**
     * 添加选课消息队列
     */
    public static final String XC_LEARNING_ADDCHOOSECOURSE = "xc_learning_addchoosecourse";
    /**
     * 完成添加选课消息队列
     */
    public static final String XC_LEARNING_FINISHADDCHOOSECOURSE = "xc_learning_finishaddchoosecourse";
    /**
     * 添加选课路由key
     */
    public static final String XC_LEARNING_ADDCHOOSECOURSE_KEY = "addchoosecourse";
    /**
     * 完成添加选课路由key
     */
    public static final String XC_LEARNING_FINISHADDCHOOSECOURSE_KEY = "finishaddchoosecourse";

    /**
     * 交换机配置 (交换机)
     * @return the exchange
     */
    @Bean(EX_LEARNING_ADDCHOOSECOURSE)
    public Exchange EX_DECLARE() {
        return ExchangeBuilder.directExchange(EX_LEARNING_ADDCHOOSECOURSE).durable(true).build();
    }

    /**
     * 交换机配置 (交换机)
     * @return the exchange
     */
    @Bean(EX_LEARNING_SCHEDULE)
    public Exchange EX_SCHEDULE() {
        return ExchangeBuilder.directExchange(EX_LEARNING_SCHEDULE).durable(true).build();
    }

    /**
     * 声明队列 （添加选课队列）
     * @author: olw
     * @Date: 2020/10/24 14:22
     * @returns: org.springframework.amqp.core.Queue
    */
    @Bean(XC_LEARNING_ADDCHOOSECOURSE)
    public Queue QUEUE_DECLARE_ADD() {
        Queue queue = new Queue(XC_LEARNING_ADDCHOOSECOURSE,true,false,true);
        return queue;
    }

    /**
     * 声明队列 （完成选课队列）
     * @author: olw
     * @Date: 2020/10/24 14:22
     * @returns: org.springframework.amqp.core.Queue
     */
//    @Bean(XC_LEARNING_FINISHADDCHOOSECOURSE)
//    public Queue QUEUE_DECLARE_FINISHADD() {
//        Queue queue = new Queue(XC_LEARNING_FINISHADDCHOOSECOURSE,true,false,true);
//        return queue;
//    }

    /**
     * 声明队列 （更新课程进度队列）
     * @author: olw
     * @Date: 2020/10/24 14:22
     * @returns: org.springframework.amqp.core.Queue
     */
    @Bean(XC_LEARNING_SCHEDULE)
    public Queue QUEUE_DECLARE_SCHEDULE() {
        Queue queue = new Queue(XC_LEARNING_SCHEDULE,true,false,true);
        return queue;
    }

    /**
     * 绑定队列到交换机 (添加选课)
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding binding_queue_media_processtask_ADD(@Qualifier(XC_LEARNING_ADDCHOOSECOURSE) Queue queue, @Qualifier(EX_LEARNING_ADDCHOOSECOURSE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(XC_LEARNING_ADDCHOOSECOURSE_KEY).noargs();
    }

    /**
     * 绑定队列到交换机 (完成选课)
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
//    @Bean
//    public Binding binding_queue_media_processtask_FINISHADD(@Qualifier(XC_LEARNING_FINISHADDCHOOSECOURSE) Queue queue, @Qualifier(EX_LEARNING_ADDCHOOSECOURSE) Exchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(XC_LEARNING_FINISHADDCHOOSECOURSE_KEY).noargs();
//    }

    /**
     * 绑定队列到交换机 (更新课程进度队列)
     * @param queue    the queue
     * @param exchange the exchange
     * @return the binding
     */
    @Bean
    public Binding binding_queue_SCHEDULE(@Qualifier(XC_LEARNING_SCHEDULE) Queue queue, @Qualifier(EX_LEARNING_SCHEDULE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(XC_LEARNING_SCHEDULE_KEY).noargs();
    }
}
