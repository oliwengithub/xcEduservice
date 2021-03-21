package com.xuecheng.send;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author MaiBenBen
 */
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描common下的所有类
@SpringBootApplication
public class SendApplication {

    public static void main (String[] args) {
        SpringApplication.run(SendApplication.class, args);
    }

}
