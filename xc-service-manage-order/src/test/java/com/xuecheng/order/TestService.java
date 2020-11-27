package com.xuecheng.order;

import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.XcOrdersPay;
import com.xuecheng.order.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestService {

    @Autowired
    OrderService orderService;

    @Test
    public void testCheckIsBuyCourse () {
        String userId = "49";
        String courseId = "4028e58161bd22e60161bd23672a0001";
        String status = "402001";
        XcOrdersPay ordersPay = orderService.checkIsBuyCourse(userId, courseId, null);
        System.out.println(ordersPay);
    }

}
