package com.xuecheng.order;


import com.sun.deploy.xml.CustomParser;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.utils.GenerateOrderNum;
import com.xuecheng.order.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.events.Event;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPay {

    @Autowired
    private PayService payService;

    @Test
    public void testPay () {
        CoursePub coursePub = new CoursePub();
        coursePub.setName("测试支付");
        int price = 1;
        //String orderNumber = new GenerateOrderNum().generate("");
        String orderNumber = "20210813143324437000";
        String ip = "192.168.1.12";
        Map<String, String> payQrcode = payService.createPayQrcode(coursePub, price, orderNumber, ip);
        System.out.println(payQrcode);
    }
}
