//package com.xuecheng.order;
//
//import com.xuecheng.framework.domain.order.XcOrdersPay;
//import com.xuecheng.framework.domain.order.request.OrderRequestList;
//import com.xuecheng.framework.model.response.QueryResponseResult;
//import com.xuecheng.order.dao.OrderMapper;
//import com.xuecheng.order.service.OrderService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class TestMapper {
//
//    @Resource
//    OrderMapper orderMapper;
//
//    @Autowired
//    OrderService orderService;
//
//    @Test
//    public void testOrderPayStatusMapper () {
//        String courseId = "4028e58161bd3b380161bd3bcd2f0000";
//        String userId = "4911";
//        String status = "402001";
//        List<XcOrdersPay> orderPayStatus = orderMapper.findOrderPayStatus(userId, courseId, status);
//        System.out.println(orderPayStatus);
//    }
//    @Test
//    public void testOrderList () {
//        OrderRequestList orderRequestList = new OrderRequestList();
//        QueryResponseResult allOrderList = orderService.findAllOrderList(0, 10, orderRequestList);
//        System.out.println(allOrderList.getQueryResult());
//    }
//}
