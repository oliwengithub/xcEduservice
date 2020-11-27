package com.xuecheng.order.controller;

import com.xuecheng.api.order.OrderControllerApi;
import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.request.CreateOrderRequest;
import com.xuecheng.framework.domain.order.request.OrderRequestList;
import com.xuecheng.framework.domain.order.response.CreateOrderResult;
import com.xuecheng.framework.domain.order.response.OrderCode;
import com.xuecheng.framework.domain.order.response.OrderResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.order.dao.XcOrdersRepository;
import com.xuecheng.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author: olw
 * @date: 2020/11/3 17:51
 * @description:  订单服务
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController implements OrderControllerApi {

    @Autowired
    private OrderService orderService;

    @Override
    @PostMapping("/list/{page}/{size}")
    public QueryResponseResult findOrderList (@PathVariable("page") int page, @PathVariable("size") int size, OrderRequestList orderRequestList) {
        XcOauth2Util oauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt jwt = oauth2Util.getUserJwtFromHeader(request);
        if (jwt != null) {
            orderRequestList.setUserId(jwt.getId());
        }
        return orderService.findOrderList(page, size, orderRequestList);
    }

    @Override
    @GetMapping("/get/{orderId}")
    public OrderResult getOrderById (@PathVariable("orderId") String orderId) {
        return orderService.getOrderById(orderId);
    }


    @Override
    @PostMapping("/create")
    public CreateOrderResult createOrders (@RequestBody CreateOrderRequest createOrderRequest) {
        XcOauth2Util oauth2Util = new XcOauth2Util();
        XcOauth2Util.UserJwt jwt = oauth2Util.getUserJwtFromHeader(request);
        if (jwt == null || createOrderRequest == null) {
            return new CreateOrderResult(OrderCode.ORDER_ADD_ORDERNUMERROR, null);
        }
        createOrderRequest.setUserId(jwt.getId());
        return orderService.createOrders(createOrderRequest);
    }
}
