package com.xuecheng.order.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.HystrixCommandResponseFromCache;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.XcOrdersDetail;
import com.xuecheng.framework.domain.order.XcOrdersPay;
import com.xuecheng.framework.domain.order.request.CreateOrderRequest;
import com.xuecheng.framework.domain.order.request.OrderRequestList;
import com.xuecheng.framework.domain.order.response.CreateOrderResult;
import com.xuecheng.framework.domain.order.response.OrderCode;
import com.xuecheng.framework.domain.order.response.OrderResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.utils.GenerateOrderNum;
import com.xuecheng.order.client.SearchClient;
import com.xuecheng.order.dao.OrderMapper;
import com.xuecheng.order.dao.XcOrdersDetailRepository;
import com.xuecheng.order.dao.XcOrdersPayRepository;
import com.xuecheng.order.dao.XcOrdersRepository;
import org.apache.commons.lang.CharRange;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author: olw
 * @date: 2020/11/3 18:41
 * @description:
 */
@Service
public class OrderService {

    private static  final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);


    @Autowired
    private XcOrdersRepository xcOrdersRepository;

    @Resource
    private OrderMapper orderMapper;

    @Autowired
    private XcOrdersDetailRepository xcOrdersDetailRepository;

    @Autowired
    private XcOrdersPayRepository xcOrdersPayRepository;

    @Autowired
    private SearchClient searchClient;

    /**
     * 查询用户订单
     * @author: olw
     * @Date: 2020/11/3 18:42
     * @param page
     * @param size
     * @param orderRequestList
     * @returns: com.xuecheng.framework.model.response.QueryResponseResult
     */
    public QueryResponseResult findOrderList (int page, int size, OrderRequestList orderRequestList) {
        if (orderRequestList == null) {
            orderRequestList = new OrderRequestList();
        }
        // 构建分页对象
        page = page == 0 ? page : page-1;
        size = size == 0 ? 10 : size;
        Pageable pageable = new PageRequest(page, size);
        Page<XcOrders> orders = xcOrdersRepository.findAllByUserId(pageable, orderRequestList.getUserId());
        QueryResult<XcOrders> queryResult = new QueryResult<>();
        queryResult.setList(orders.getContent());
        queryResult.setTotal(orders.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }

    /**
     * 根据订单id查新订单
     * @author: olw
     * @Date: 2020/11/3 19:12
     * @param orderId
     * @returns: com.xuecheng.framework.domain.order.XcOrders
     */
    public XcOrders findOrderById (String orderId) {
        Optional<XcOrders> optional = xcOrdersRepository.findById(orderId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 创建订单 (添加订单，订单支付，订单详情)
     * @author: olw
     * @Date: 2020/11/4 16:13
     * @param createOrderRequest
     * @returns: com.xuecheng.framework.domain.order.response.CreateOrderResult
     */
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderResult createOrders (CreateOrderRequest createOrderRequest) {

        String userId = createOrderRequest.getUserId();
        String courseId = createOrderRequest.getCourseId();
        // 根据根据用户判断是否已经创建订单 如果存在订单直接根据用户信息返回订单信息
        XcOrdersPay ordersPay = this.checkIsBuyCourse(userId, courseId, null);
        if (ordersPay != null) {
            Optional<XcOrders> optional = xcOrdersRepository.findById(ordersPay.getOrderNumber());
            return  new CreateOrderResult(CommonCode.SUCCESS, optional.get());
        }

        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(OrderCode.ORDER_ADD_ITEMISNULL);
        }
        // 根据课程id获取课程信息 到es查询课程信息
        Map<String, CoursePub> all = searchClient.getAll(courseId);
        if (all == null || all.get(courseId) == null) {
            ExceptionCast.cast(OrderCode.ORDER_ADD_GETCOURSEERROR);
        }
        CoursePub coursePub = all.get(courseId);

        // 创建课程订单id
        GenerateOrderNum generateOrderNum = new GenerateOrderNum();
        String generate = generateOrderNum.generate("");
        XcOrders orders = new XcOrders();
        // 构建订单详情数据
        XcOrdersDetail ordersDetail = new XcOrdersDetail();

        orders.setOrderNumber(generate);
        orders.setUserId(userId);
        orders.setCreateTime(new Date());
        // 定价 相当于课程的原价
        orders.setInitialPrice(coursePub.getPrice_old());
        orders.setPrice(coursePub.getPrice());
        // 支付状态 401001 未支付 401002 支付成功 401003 取消支付
        orders.setStatus("401001");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY‐MM‐dd");
        String startTime = coursePub.getStartTime();
        String endTime = coursePub.getEndTime();
        try {
            if (StringUtils.isNotEmpty(startTime)) {
                orders.setStartTime(simpleDateFormat.parse(startTime));
                ordersDetail.setStartTime(simpleDateFormat.parse(startTime));
            }
            if (StringUtils.isNotEmpty(endTime)) {
                orders.setEndTime(simpleDateFormat.parse(endTime));
                ordersDetail.setEndTime(simpleDateFormat.parse(endTime));
            }
        }catch (Exception e) {
            // 时间转换异常忽略
            LOGGER.error("时间转换出错",e);
        }
        // 先保存 因为有外键约束
        xcOrdersRepository.save(orders);
        ordersDetail.setUserId(userId);
        ordersDetail.setOrderNumber(generate);
        ordersDetail.setCoursePrice(coursePub.getPrice());
        ordersDetail.setValid(coursePub.getValid());
        ordersDetail.setCourseNum(1);
        ordersDetail.setCourseId(courseId);
        xcOrdersDetailRepository.save(ordersDetail);
        // 生成订单详情信息
        List<XcOrdersDetail> ordersDetails = new ArrayList(){{add(ordersDetail);}};
        String detail = JSON.toJSONString(ordersDetails);
        // 保存订单信息
        orders.setDetails(detail);
        xcOrdersRepository.save(orders);
        // 保存订单支付信息
        XcOrdersPay xcOrdersPay = new XcOrdersPay();
        xcOrdersPay.setOrderNumber(generate);
        // 402001未支付 402002支付成功 402003已关闭 402004支付失败
        xcOrdersPay.setStatus("402001");
        xcOrdersPayRepository.save(xcOrdersPay);
        return new CreateOrderResult(CommonCode.SUCCESS, orders);
    }


    /**
     * 检验当前用户是否已经购买或已经创建购买课程的订单
     * @author: olw
     * @Date: 2020/11/5 15:39
     * @param userId
     * @param courseId
     * @param status 订单支付状态 402 001未支付 002支付成功 003已关闭 004支付失败
     * @returns: com.xuecheng.framework.domain.order.XcOrders
     */
    public XcOrdersPay checkIsBuyCourse (String userId, String courseId, String status) {
        List<XcOrdersPay> orderPayStatus = orderMapper.findOrderPayStatus(userId, courseId, status);
        if (orderPayStatus != null && orderPayStatus.size() > 0) {
            return orderPayStatus.get(0);
        }
        return null;
    }

    /**
     * 根据用户id和订单查询订单
     * @author: olw
     * @Date: 2020/11/5 20:22
     * @param userId
     * @param orderNumber
     * @returns: com.xuecheng.framework.domain.order.XcOrders
     */
    public XcOrders findOrderByUserIdAndOrderNumber (String userId, String orderNumber) {
        return xcOrdersRepository.findXcOrdersByUserIdAndOrderNumber(userId, orderNumber);
    }

    /**
     * 根据订单id查询订单
     * @author: olw
     * @Date: 2020/11/13 17:28
     * @param orderId
     * @returns: com.xuecheng.framework.domain.order.response.OrderResult
     */
    public OrderResult getOrderById (String orderId) {
        XcOrders xcOrders = this.findOrderById(orderId);
        if (xcOrders == null) {
            return new OrderResult(OrderCode.ORDER_FINISH_NOTFOUNDORDER,null);
        }
        return new OrderResult(CommonCode.SUCCESS, xcOrders);
    }

    public QueryResponseResult findAllOrderList (int page, int size, OrderRequestList orderRequestList) {
        if (orderRequestList == null) {
            orderRequestList = new OrderRequestList();
        }
        // 构建分页对象
        page = page == 0 ? page : page-1;
        size = size == 0 ? 10 : size;

        PageHelper.startPage(page, size);
        com.github.pagehelper.Page<XcOrders> allOrderList = orderMapper.findAllOrderList(orderRequestList);
        QueryResult<XcOrders> queryResult = new QueryResult<>();
        queryResult.setList(allOrderList.getResult());
        queryResult.setTotal(allOrderList.getTotal());
        return new QueryResponseResult<XcOrders>(CommonCode.SUCCESS, queryResult);


    }
}
