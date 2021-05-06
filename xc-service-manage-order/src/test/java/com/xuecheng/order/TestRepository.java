//package com.xuecheng.order;
//
//import com.xuecheng.framework.domain.order.XcOrders;
//import com.xuecheng.framework.domain.order.XcOrdersDetail;
//import com.xuecheng.order.dao.XcOrdersDetailRepository;
//import com.xuecheng.order.dao.XcOrdersRepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class TestRepository {
//
//    @Autowired
//    XcOrdersRepository xcOrdersRepository;
//
//    @Autowired
//    XcOrdersDetailRepository xcOrdersDetailRepository;
//
//    @Test
//    public void testOrdersRepository() {
//
//        Pageable pageable = new PageRequest(1,10);
//        String userId = "49";
//
//        Page<XcOrders> orders = xcOrdersRepository.findAllByUserId(pageable, userId);
//        List<XcOrders> content = orders.getContent();
//        System.out.println(content);
//    }
//
//    @Test
//    public void testOrdersDetailRepository() {
//    }
//}
