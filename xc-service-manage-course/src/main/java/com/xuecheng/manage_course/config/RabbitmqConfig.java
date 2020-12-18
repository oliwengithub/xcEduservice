package com.xuecheng.manage_course.config;

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
     * 交换机配置使用direct类型
     * @return the exchange
     */
    @Bean(EX_LEARNING_SCHEDULE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        return ExchangeBuilder.directExchange(EX_LEARNING_SCHEDULE).durable(true).build();
    }
}
