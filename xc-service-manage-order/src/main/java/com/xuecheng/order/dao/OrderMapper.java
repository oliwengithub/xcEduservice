package com.xuecheng.order.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.order.XcOrders;
import com.xuecheng.framework.domain.order.XcOrdersPay;
import com.xuecheng.framework.domain.order.request.OrderRequestList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @author: olw
 * @date: 2020/11/15 15:27
 * @description:  订单复杂查询
 */
@Mapper
public interface OrderMapper {
    /**
     * 查询当前用户是否已经购买或创建购买课程的订单
     * @author: olw
     * @Date: 2020/11/15 15:45
     * @param userId
     * @param courseId
     * @param status
     * @returns: java.util.List<com.xuecheng.framework.domain.order.XcOrdersPay>
    */
    public List<XcOrdersPay> findOrderPayStatus (@Param("userId") String userId, @Param("courseId") String courseId, @Param("status")String status);

    /**
     * 后台获取所有订单列表
     * @author: olw
     * @Date: 2021/4/13 15:56
     * @param orderRequestList
     * @returns: com.github.pagehelper.Page<com.xuecheng.framework.domain.order.XcOrders>
    */
    public Page<XcOrders> findAllOrderList (OrderRequestList orderRequestList);


}
