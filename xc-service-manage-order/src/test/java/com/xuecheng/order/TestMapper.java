package com.xuecheng.order;

import com.xuecheng.framework.domain.order.XcOrdersPay;
import com.xuecheng.order.dao.OrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMapper {

    @Resource
    OrderMapper orderMapper;

    @Test
    public void testOrderPayStatusMapper () {
        String courseId = "4028e58161bd3b380161bd3bcd2f0000";
        String userId = "4911";
        String status = "402001";
        List<XcOrdersPay> orderPayStatus = orderMapper.findOrderPayStatus(userId, courseId, status);
        System.out.println(orderPayStatus);
    }
}
