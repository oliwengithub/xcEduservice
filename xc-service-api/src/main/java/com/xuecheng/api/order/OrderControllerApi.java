package com.xuecheng.api.order;

import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.request.CreateOrderRequest;
import com.xuecheng.framework.domain.order.request.OrderRequestList;
import com.xuecheng.framework.domain.order.response.CreateOrderResult;
import com.xuecheng.framework.domain.order.response.OrderResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("订单服务接口")
public interface OrderControllerApi {

    @ApiOperation("获取用户订单列表")
    public QueryResponseResult findOrderList (int page , int size, OrderRequestList orderRequestList);

    @ApiOperation("获取所有订单列表")
    public QueryResponseResult findAllOrderList (int page , int size, OrderRequestList orderRequestList);

    @ApiOperation("根据订单id获取订单信息")
    public OrderResult getOrderById (String orderId);

    @ApiOperation("创建课程订单")
    public CreateOrderResult createOrders(CreateOrderRequest createOrderRequest);


}
